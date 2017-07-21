# reframe-fsm

A collection of example [reframe]() projects that implement the following
requirements. Actually, they don't quite yet, but soon will.

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

* Mock success by showing an alert of success when there are no errors

## Examples

* example-no-fsm
* example-fsm

## Usage

From the `user` namespace at the REPL:

```clojure
user=> (go) ;; start webserver
...
user=> (watch-cljs) ;; start cljsbuild auto
...
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
