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

[NOTE] This is just a rough, stream of consciousness first pass.

First of all, let's review a simplistic representation of a state machine
Clojure data.

First step, do some design! You're probably used to building UI by hacking
on some components, maybe tweaking their rendering, then wiring them
up with events, modifying state as needed. The first part can be done the
same way. It's the way we think about state that changes. It requires
some up-front thinking.

Talk about Adam's observation. At first glance, it seems you'd start by
drawing the states, then adding the transitions. You try that, realize
it doesn't work, then start from the transitions. When you're building UI,
which is almost always event-driven, transitions are easy.

Transitions are user events (and system actions, like ajax responses?).

Eventually, you realize that the states of your UI is a subset of unique
combinations of state of all elements of that UI. There may be some
combinations of element state that are not valid.

Tip #1: Your FSM data shouldn't be more than a page of code.

Tip #2: Keep like things together. [NOTE] Review Horrocks for this.

Tip #3: ??

## State Machines in Reframe

## Logging In

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

    Final Thoughts
        Mostly top down & a little bottom-up?
        Re: final thoughts of previous article


