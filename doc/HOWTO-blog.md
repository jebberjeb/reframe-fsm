# Restate Your UI: Creating a User Interface with Reframe and State Machines

Earlier this year our team started experimenting with state machines in our
user interface programming.  After a few months of unusually tolerable UI
work, dozens of "Aha!" moments, and some (lots of) back patting, we looked
back and realized this pattern had transformed our codebase.  A consistent
approach to design and a simple high-level abstraction had made extending
each other's code a piece of cake.

Last time, we presented this state machine based approach to UI
programming.  We discussed some of the problems it solves by contrasting it
with a more traditional bottom-up, ad hoc approach.  In this article, we
you through a complete example of how to apply this technique, using a
simple login UI.


## State Machine Design

;; TODO - rewrite this paragraph, talk specifically about web apps, should
;; decrease ambiguity, and need for explanation.
;; TODO - also, punch it up!

Like most of us, when handed a UI to program, you probably start by writing
code to render things on the screen (drop downs, buttons, etc). From there, we
usually compose these UI elements together, maybe we work on layout. Once we've
got a meaningful amount of the UI components -- commontly called the rendering
code, the view, DOM, etc -- in place, we start wiring it together with events.
As we add events, we also begin to think about, and probably just hack together
ad hoc, the application's state. That's your typical, widely accepted,
bottom-up approach to UI programming.

;; NOTE - it's fine to write rendering code bottom-up or ad hoc. That doesn't
;; change.

The design approach presented here is different. Before writing any code, we're
going to do some top-down design. We'll examine the UI's events and its state
in order to build a high-level model of our UI -- a state transition diagram.

;; TODO clean this up, punch it up.

On first glance, it might seem that to build a state transition diagram, you'd
start by drawing the states, then connecting them with transitions.  You try
it, it doesn't work, so you try again, starting with the transitions.  We know,
from the previous article, that user actions & system events become the
transitions of the state machine. When you're building UI, which is almost
always event-driven, then transitions are easy.

;; NOTE This is just a rough, stream of consciousness first pass. A walkthrough
;; of my thought process in actually designing the STD.

First, we need a statement of requirements. And I'm not sure where this should
go, but putting it here for now. Maybe just link to it, put it in /docs.

* When login button is clicked
  * If email is blank, display error "email required"
  * Remove "email required" error when email is changed
  * If password is blank, display error "password required"
  * Remove "password required" error when password is changed
* If email doesn't exist display error "user does not exist"
* Remove "user does not exist" error when email is changed
* If password not valid display error "invalid password"
* Remove "invalid password" error when password is changed
* Disable login button on submit request
* Disable login button if error is present
* Enable login button if error is removed

Do we need this one? Is there a better way to mock it? alert?

* Redirect them to /profile on successful submit response

I start by making a table of the individual elements who's state may
change. I do that because I already understand that, for a UI, the
"states" is the set of all distinct combinations of the individual elements'
states -- minus combinations that aren't valid. For example, in our app,
having multiple error messages visible at the same time isn't valid.

We don't include the email and password inputs because their state isn't
going to change (other than their value). They don't ever get disabled.


|Email Req  |Pwd Req|Not Exists |Bad Pwd|Login Button
------------+-------+-----------+-------+-----------
?            

Then I realize I don't know how to add "submitting" to this thing. So
maybe starting from all the possible combos, then filtering down isn't
quite right. Besides this chart would be mostly useless.

So I take another approach. I think about all of the possible actions
I can take, all of the use cases. So, rather than listing all the states
out, then connecting them, I'm really building the transition diagram
as I go, and I'm also building up that table at the same time -- just
to contain the details of each state.

So, update the table to tie them together.

State    |Email Req  |Pwd Req|Not Exists |Bad Pwd|Login Button
---------+-----------+-------+-----------+-------+------------

Then, starting from an initial state (we'll call it "Ready" to be
consisten with previous post), think of all the possible actions.
(First, we add a state to the table for "Ready", and start drawing
our state transition diagram with the first state).

[start] -init-> Ready


State    |Email Req  |Pwd Req|Not Exists |Bad Pwd|Login Button
---------+-----------+-------+-----------+-------+------------
Ready     -           -       -           -       Enabled

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


## State Machines in Reframe

;; NOTE This is just a rough, stream of consciousness first pass.

Reframe is great, we love it. It's our favorite of the React libraries for
Clojurescript. It also suits new Clojure programmers pretty well. It has
great documentation. It's core, React, is a pretty intuitive adaptation
of React to idiomatic Clojure.

It's a natural fit with state machines (I think I remember seeing something
about this in their own documentation). See Horrocks 35-37. Reframe's
(as well as Redux's) pure rendering functions, which are functions of state
work well here. Immutable data is also an intuitive match to the
state machine's core concept: F(current state, event) = Next State.

Reframe's core concepts map well to state machines. States are related to
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

    ** Do we need to introduce Reframe in the intro, or can it wait until
    we get to the Reframe specific stuff?

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

    Reframe
        Introduce it
        popular choice, probably second to Om
        Personally, Of Om, raw Reagent and Reframe, Quiesence Reframe is my
        favorite -- why?
            docs
            builds on reagent
            core concepts a natural fit with state machine model [find link]
        Transitions = Reframe events
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
