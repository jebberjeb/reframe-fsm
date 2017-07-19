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


## State Machine Design

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
we go.

As each new state is discovered, we'll add it to a table which describes it.
The UI starts out with all of its text fields and buttons enabled.  We'll name
that state "Ready".

State | Error Message | Login Button
------+---------------+-------------
Ready   ""              Enabled

;; Draft 0 starts here

;; TODO - we've moved requirements, need to refer to them here

Then, starting from an initial state (we'll call it "Ready" to be
consisten with previous post), think of all the possible actions.
(First, we add a state to the table for "Ready", and start drawing
our state transition diagram with the first state).

[start] -init-> Ready


State    |Email Req  |Pwd Req|Not Exists |Bad Pwd|Login Button
---------+-----------+-------+-----------+-------+------------
Ready     -           -       -           -       Enabled

need words ...

State: Ready
Action: Click submit
Result: "Email required" error is displayed
New State: Email Required

Next, we do two things. (1) We add a state to the table.

State      |Email Req  |Pwd Req|Not Exists |Bad Pwd|Login Button
-----------+-----------+-------+-----------+-------+------------
Ready       -           -       -           -       Enabled
*Email_Req  X           -       -           -       Disabled

(2) We draw a transition for this action to the new state on the
STD.

[start] -init-> Ready -click-> Email_Req

We ask if there are any other actions that can be taken from Ready,
and the answer is: no. So we move to another state, Email_Req and
think of all possible actions.

We know that they can't click submit, because it has been disabled
(per our table & requirements). We also know that if they type in
the password field, nothing will really change. If they type in
the email field however, the error message will go away.

What is the new state in this case? We consult the table and
realize that we'd be back at the Ready state.

State: Email_Req
Action: Change Email
Result: "Email required" error is removed
New State: Ready

We don't need to add anything to the table, but we do need to
add another transition to our STD.

[start] -init-> Ready <-change email- -click-> Email_Req

So, we'd previous thought there were no more transitions from
the ready state -- we were wrong. There have to be more. We
hadn't thought of the case where the email input contains
something. It turns out there are a few more things that can
happen here:

* An invalid username could be entered into email field
* A valid username could be entered into email field, but no password
* A valid username with an invalid password
* A valid username with a valid password!

We perform the same type of analysis for each of these cases,
and end up with the following table & chart.

;; TODO show completed table & chart
;; NOTE is this enough detail? Should we have manually walked them
;; through another transition?

Show the actual Clojure data representation.

Tip #1: Your FSM data shouldn't be more than a page of code.

Tip #2: Keep like things together. [NOTE] Review Horrocks for this.

Tip #3: ??


## State Machines in Re-frame

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


## Thanks for Logging In!

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


## Final Thoughts



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
