# Login Example Re-frame FSM-based UI

This is an example login UI, using the Re-frame library, with an FSM-based
approach.


## Requirements

* When login button is clicked
  * If email is blank, display error "email required"
  * If password is blank, display error "password required"
  * If email doesn't exist display error "user does not exist"
  * If password not valid display error "invalid password"

* Remove "password required" error when password is changed
* Remove "email required" error when email is changed
* Remove "user does not exist" error when email is changed
* Remove "invalid password" error when password is changed

* Login button
  * Is disabled on submit request
  * Is enabled when submit request finished
  * Is disabled when error present
  * Is enabled when no error present


## Usage

```
$> lein repl
user=> (go) ;; start the Pedestal server
user=> (build-cljs) ;; build the app
```

The application will be available at `http://localhost:8080/index.html`.


## Dependencies

I've deliberately left out Figwheel, Re-frisk, and other useful tooling. My
goal was to show a spartan example of the FSM based approach, attempting to
avoid any incidental complexity.

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
