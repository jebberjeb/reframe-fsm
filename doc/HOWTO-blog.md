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


## State Machines in Re-Frame -or- ??

;; DRAFT 0 START

;; Might want to say that if you're interested in Re-frame basics, look
;; at their docs, or Eric Normand's new guide.
;; https://purelyfunctional.tv/guide/re-frame-building-blocks/

;; NOTE This is just a rough, stream of consciousness first pass.

Re-frame is great, we love it. It's our favorite of the React libraries for
Clojurescript. It also suits new Clojure programmers pretty well. It has
great documentation. It's core, React, is a pretty intuitive adaptation
of React to idiomatic Clojure.

It's a natural fit with state machines (I think I remember seeing something
about this in their own documentation). See Horrocks 35-37. Re-frame's
(as well as Redux's) pure rendering functions, which are functions of state
work well here. Immutable data is also an intuitive match to the
state machine's core concept: F(current state, event) = Next State.

Re-frame's core concepts map well to state machines. States are related to
the pure rendering functions, or immutable app state data. Transitions
are events. Subscriptions?

Introduce the `next-state` function and any other generic tooling required
for this example.


## The Code -or- ??

;; NOTE This is just a rough, stream of consciousness first pass.

Refer to previous design, the state diagram.

Write the code for the first state, which should be mostly rendering and
subscriptions.

Plumbing to set the initial state.

Then, do the first real transition, which should be an event, followed by
updates to the subscriptions & rendering.

Walk through the rest of the events, show them, or simply link to the code.

Done?

Maybe add/change a feature.


## Final Thoughts -or- Thanks for Logging In!



## Outline & Notes

    ;; general -> specific
    ;; HOWTO -> top-down design -> framework stuff -> specifics

    Intro
        Our experiences
        Last time we talked about X technique / approach.
        This time we're going to show you how to apply this technique to
          a simple application.

    ** Do we need to introduce Re-frame in the intro, or can it wait until
    we get to the Re-frame specific stuff?

    Design
        Top down
        Start by drawing the FSM (sketch or data)
        States (distinct combinations of UI element states)
            start w/ states, realize that doesn't work, then start w/ transitions
            transitions are usually obvious
        Transitions user actions
        Guidelines / Tips
            Not too many states
            Keep like things together

    Re-frame
        Introduce it
        popular choice, probably second to Om
        Personally, Of Om, raw Reagent and Re-frame, Quiesence Re-frame is my
        favorite -- why?
            docs
            builds on reagent
            core concepts a natural fit with state machine model [find link]
        Transitions = Re-frame events
        `next-state` function

    The Code
        Don't need to show all code, but link to it all.
        Refer to decisions from design section, maybe show diagram again

    Final Thoughts
        Mostly top down & a little bottom-up?
        Re: final thoughts of previous article


    NOTE: words

    "app state" not "application state"
    "UI" not "user interface" (except the first reference)
    Don't use "application"
    the user does "actions"
    the UI generates & handles "events"
    "Re-frame", not reframe re-frame Reframe
