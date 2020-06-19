- [echo-server](#orgfc58024)
  - [Usage example](#org528281c)


<a id="orgfc58024"></a>

# echo-server

Toy echo server

<a id="org528281c"></a>

## Usage example

-   shell
    
        # navigate to project dir
        # ...
        
        # start repl
        lein repl
-   repl
    
        echo-server.core> (start-server)
-   shell
    
        # load some data
        curl -s -d '{"x": 42}' http://localhost:8889
        
        # inspect data
        curl -si http://localhost:8889/echo
