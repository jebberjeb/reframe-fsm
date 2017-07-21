# Restate Your UI: Creating a User Interface with Re-frame and State Machines

Earlier this year our team started experimenting with state machines in our
user interface programming.  After a few months of unusually tolerable UI work,
dozens of "Aha!" moments, and some (lots of) back patting, we looked back and
realized this pattern had transformed our codebase.  A consistent approach to
design and a simple high-level abstraction had made extending each other's code
a piece of cake.

Last time, we presented this state machine based approach to UI programming.
We discussed some of the problems it solves by contrasting it with a more
traditional bottom-up, ad hoc approach.  In this article, we take you through a
complete example of how to apply this technique using a simple login UI. We're
going to use Clojurescript and, since this will be a React app, we'll use
Re-frame.


## State Machine Design -or- Modeling Your UI -or- ??

Prior to our enlightenment, when building a new React UI, we usually began by
writing code to render the things on the screen -- buttons, drop-downs, lists
-- then composed them into views.  Once we had enough of this rendering code in
place, we wired things together with events.  At some point, we were forced to
add state to our UI. In other words, we did bottom-up design.

;; TODO - link STD

The design approach presented here is different.  We're going to do some
top-down design before writing a line of code.  We'll examine the UI's events
and its states in order to build a high-level model using a [State Transition
Diagram]().

;; TODO - show an example state transition diagram graphic

It's tempting to try to list out all of the UI's states then connect them.
We're not going to do that. We're building UI. UI is event-driven.  We know
from the previous article that user actions (and system events) become the
transitions of the State Transition Diagram.  So we'll start there.  We'll walk
through all of the things a user can do, discovering all of our UI's states as
we go.  As each new state is discovered, we'll add it to a table which
describes it.  The UI starts out with all of its text fields and buttons
enabled.  We'll name that state "Ready", and make it our State Transition
Diagram's starting state.

State | Error Message | Login Button
------+---------------+-------------
Ready   ""              Enabled

[start] --> Ready

There are really only two things a user can do from our `Ready` state: type
some text into a field, or click the Login button.  Buttons are meant to be
clicked, so let's go with that.  Let's call that action `click_login_button`.
The result, per our [requirements](README.md#requirements), is to display the
error "email required" and to disable the Login button.  Since our UI's
appearance has changed, that means we've moved to a new state state, which
we'll call `Email_Required`. Let's add the details about `Email_Required` to
our table, as well as our State Transition Diagram.

State          | Error Message   | Login Button
---------------+-----------------+-------------
Ready            ""                Enabled
Email_Required   "email required"  Disabled

[start] --> Ready -click_login_button-> Email_Required

From our `Email_Required` state, we again try to image all the actions a user
can take. Since the Login button is disabled, their only options are to enter
an email or password.  Consulting our [requirements]() tells us that, at this
point, nothing interesting happens if they change the password.  However, there
is a requirement to _Remove "email required" error when email is changed_.
We'll call that action `change_email`.  There's also a general requirement that
the _Login button is enabled when no error present_.  Our UI's appearance has
changed again, back to what it looked like in the `Ready` state. Once again,
let's update our table and State Transition Diagram.

State          | Error Message   | Login Button
---------------+-----------------+-------------
Ready            ""                Enabled
Email_Required   "email required"  Disabled

[start] --> Ready <-change_email- -click_login_button-> Email_Required

Alright, we're not going to drag you through the entire exercise, but let's
do one more transition, and we'll move on.

Back in the `Ready` state, our email field now contains some text.  If we click
that irresistable Login button again, the rules say that, since the password is
blank, to display a "password required" error message, and to disable the
Login button.  We'll call our new state, `Password_Required`, and we'll call
this action `click_login_no_password` (we'll also update our other action names
to make them more specific).  One last time, we'll update our table and State
Transition Diagram.

State            | Error Message      | Login Button
-----------------+--------------------+-------------
Ready              ""                   Enabled
Email_Required     "email required"     Disabled
Password_Required  "password required"  Disabled

[start] --> Ready <-change_email- -click_login_no_email-> Email_Required
                  -click_login_no_password-> Password_Required

Building up our State Transition Diagram incrementally helps us to vet
requirements up front (you can probably spot some ambiguities in them), as well
as raise UX questions early. More importantly, we're able to construct a fairly
complete model of our UI before any real coding happens.

State            | Error Message        | Login Button
-----------------+----------------------+-------------
Ready              ""                     Enabled
Email_Required     "email required"       Disabled
Password_Required  "password required"    Disabled
User_Not_Exist     "user does not exist"  Disabled
Invalid_Password   "invalid password"     Disabled
Loggin_In          ""                     Disabled

[start] --> Ready <-change_email- -click_login_no_email-> Email_Required
                  -click_login_no_password-> Password_Required

;; TODO Add transitions to and from the new states, making sure to add the
;; `Logging_In` state.

The resulting State Transition Diagram can be represented using simple Clojure
data literals.

```clojure
{nil                {:init               :ready}
 :ready             {:login-no-password  :password-required
                     :login-no-email     :email-required
                     :try-login          :logging-in}
 :logging-in        {:login-bad-password :invalid-password
                     :login-no-user      :user-not-exist
 :email-required    {:email-changed      :ready}
 :password-required {:password-changed   :ready}
 :user-not-exist    {:email-changed      :ready}
 :invalid-password  {:password-changed   :ready}
```

Before we dive into the code, let's go over a few things we've learned which
might help you when using this design approach.

_TIP #1 Your state machine shouldn't require more than a page of Clojure
code._

Your first goal with this approach is building a useful model -- something that
describes your UI at a glance.  Beyond this size, you should consider splitting
them up.  Generating a diagram from your state machine's data can help with
this, but we've found that right around the time the code becomes hard to read,
so does the diagram.

* _TIP #2 Keep similar things together._

You don't need to create one state machine for your entire UI, or even a single
view.  For example, if your UI has a user profile page which lets the user
update their mailing addres in one pane, and notifications settings in another,
you'll probably want to separate your state machines similarly. We'll go into
more depth on this in a future post.

* _TIP #3_

;; TODO


## Re-frame

[Re-frame](https://github.com/Day8/re-frame) is a Clojurescript library for
building React applications. While the approach presented here will work
regardless of the library or framework you're using, we think it fits
Re-frame's data oriented design particularly nicely. Re-frame's essence is a
reduction.

```clojure
(reduce handle-event app-state event-queue)
```

Where `handle-event` can be as simple as a pure function of app state and an
event, returning a new app state.

```clojure
(defn handle-login
  [db event]
  ...
  db')

;; Register event handler
(re-frame.core/reg-event-db :login handle-login)
```

This lines up perfectly with a state machine's state transition function, which
is a function of the current sate and a transition, returning the next state.

```clojure
(defn next-state
  [state-machine current-state transition]
  ...
  next-state)
```

To recap, our state machine is a map of state to a transition map.

```clojure
(def state-machine {nil      {:init   :ready}
                    :ready   {:start  :running}
                    :running {:finish :done
                              :abort  :error}})
```

With this in mind, the implementation of `next-state`, minus any
error-handling, is pretty simple.

```clojure
(defn next-state
  [state-machine current-state transition]
  (get-in state-machine [current-state transition]))
```

For our example, we'll use a convenience function which closes over our state
machine.

```clojure
(def login-state-machine { ... })

(def login-next-state (partial next-state loginstate-machine))
```

Assuming we can map our Re-frame events directly to our state-machine's
transitions (we can), we'll use a general purpose event handling function.

```clojure
(defn next-state
  [db [event-kw _]]
  (update db :state login-next-state event-kw))
```

That'll allow us to reuse the same code to handle any state change event.

```clojure
(re-frame.core/reg-event-db :email-changed next-state)
(re-frame.core/reg-event-db :password-changed next-state)
```

Even though it isn't quite this simple for every event, as we'll see, hopefully
this gives you a taste of how naturally this approach feels with Re-frame.

## The Code -or- ??

;; Draft 0

;; TODO link to the complete example source
;; TODO what do we actually want to show here?
;; TODO refer to previous diagram?

## Final Thoughts -or- Thanks for Logging In!

;; TODO Lot's of top-down, but still some bottom-up?
;; TODO reference final thoughts from previous article
