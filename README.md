# Spring permissions
[![Maven Central](https://img.shields.io/maven-central/v/io.github.encryptorcode/spring-permissions.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.encryptorcode%22%20AND%20a:%22spring-permissions%22)
[![License](https://img.shields.io/github/license/encryptorcode/spring-permissions)](https://github.com/encryptorcode/spring-permissions/blob/master/LICENSE)
[![Maintainability](https://api.codeclimate.com/v1/badges/f9cc1dc193117fd7f834/maintainability)](https://codeclimate.com/github/encryptorcode/spring-permissions/maintainability)
[![Dependabot](https://badgen.net/dependabot/encryptorcode/spring-permissions/277316022?icon=dependabot)](https://github.com/encryptorcode/spring-permissions)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.encryptorcode/spring-permissions?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/io/github/encryptorcode/spring-permissions/)

> Move all your validations out of your business logic.

## Installation
#### Maven
```xml
<dependency>
    <groupId>io.github.encryptorcode</groupId>
    <artifactId>spring-permissions</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### Groovy
```groovy
implementation 'io.github.encryptorcode:spring-permissions:1.0.0'
```

#### Kotlin DSL
```groovy
implementation("io.github.encryptorcode:spring-permissions:1.0.0")
```

## Why?
Have you ever wanted a way to separate huge piles of permission checks and validations out of your business layer ?
Have you ever felt your method sizes are way too big, but you weren't able to do anything about it ?
For the answer both the above, Spring Permissions is a layer that will help you split your services into validation and actual logic by using simple annotations.

## Usage
### Adding permission checks to controller methods
```java
@Controller
class MyController{

    @RequestMapping(path = "/myapi")
    @Permission(id = "my.permission.id")
    @ResponseBody
    public String myApi(){
        return "You are valid user.";
    }
}
```

Adding permissions to a controller is as easy adding an annotation to the controller method. 
Just add the @Permission annotation with the right permission id and permission handler method will be automatically invoked. 

### To register a permission handler
```java
import io.github.encryptorcode.permissions.abstracts.PermissionHandler;
import io.github.encryptorcode.permissions.annotations.Handler;

class MyPermissionHandler extends PermissionHandler {
    public MyPermissionHandler(Variables variables){
        super(variables);
    }
    
    @Handler(id = "my.permission.id")
    public void myPermissionValidator() throws Exception {
        if(some condition){
            throw new Exception("Seems like you don't have permission to perform this operation");
        }
    }
}
```

You need to follow certain rules when writing a permission handler
1. You can define any number of classes and methods.
2. All the classes should extend `PermissionHandler` class
3. All the classes should have a constructor with just `Variables` param
4. All the methods should be annotated with `@Handler` and permission id should be specified
5. All the methods should be of public visibility and should return void

### Setting up permission manager
#### 1. Initialising Permission Manager.
```java
PermissionManager.init("your.app.package.name");
```
Make sure you make this call immediately after your server starts. 
> In case of tomcat you will want to register a listener and invoke this once the server is started.

#### 2. Adding Interceptors and Resolvers.
```java
import io.github.encryptorcode.permissions.service.PermissionInterceptor;
import io.github.encryptorcode.permissions.service.VariablesResolver;

@EnableWebMvc
@ComponentScan(basePackages = {"your.app.package.name"})
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getPermissionInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(getPermissionVariableResolver());
    }

    @Bean
    public PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Bean
    public VariablesResolver getPermissionVariableResolver() {
        return new VariablesResolver();
    }
}
```

We need to register both `PermissionInterceptor` and `VariablesResolver` to spring. 
If you are using springs with annotation based configurations you add those in your WebMvcConfigurer class as given in the above snippet.

### [EXTRA] Passing data from permission handler to controller
In permission handler you can use the variables object provided from the constructor. 
Say for example you need to store the result of a db operation `dao.get("data")` and pass the result of this to the controller, you can do the same as below. 
```java
data = variables.pipe("name", () -> dao.get("data"));
```

Then in the controller method you can define an argument as below. This will automatically get the data you set using `variables.pipe()` in the permissions layer. 
```java
    @HandlerVariable("name") String data
```

#### `pipe(key, lambda)` method
You need to pass the key name and a lambda expression that returns the retrieved data.
lambda expression will be automatically invoked and the result will be stored in the specified key and returned.
**If this method is invoked with the same key again lambda expression WILL NOT be evaluated, so the existing data will be returned.**
If you need to forcefully execute the lambda function you may also invoke `pipeNew(key, lambda)` for the same.

### [EXTRA] Passing data from controller to permission handler
You may define arguments for your permission handler methods and you have to set argument values in args param of @Permission annotation.
Say for example, you need to pass 2 arguments the word `id` and request param value for `id` to permissions handler. You can do as follows.

**In controller**
```java
@Permission(id = "my.permission", args = {"id", "${param.id}"})
```

**In permission handler**
```java
@Handler(id = "my.permission")
public void myPermission(String id, String idParam) throw Exception{
    // your logic
}
```

#### Supported templates
* `${path.name}` → Path value taken from the url
* `${query.name}` or `${param.name}` → Request param value
* `${header.name}` → Header value
* `${cookie.name}` → Cookie value

## Example
We have made an example application for demo purposes, Feel free to clone this repository and run `mvn clean package cargo:run` inside the example folder.
A few interesting files you might like to refer from the example folder:
* [com.example.myshop.controllers.ProductsController](./example/src/main/java/com/example/myshop/controllers/ProductsController.java)
* [com.example.myshop.permissions.ProductPermissions](./example/src/main/java/com/example/myshop/permissions/ProductPermissions.java)