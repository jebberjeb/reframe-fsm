    1 HOWTO design a re-frame UI w/ state machines

        ===

        ;; We'll do general -> specific
        ;; HOWTO -> top-down design -> framework stuff -> specifics

        Intro
            Last time we talked about X technique / approach.
            This time we're going to show you how to apply this technique to
              a simple application.
            We'll use the reframe framework because it's a popular choice
            Why?? (consistent codebase, ...)
            state the mission: login UI / screen

        Design
            Top down
            Start by drawing the FSM (sketch or data)
            States (distinct combinations of UI element states)
                start w/ states, realize that doesn't work, then start w/ transitions
                transitions are usually obvious
            Transitions user actions
            Guidelines / Tips
                Keep like things together
                Not too many states

        Reframe
            Transitions = Reframe events
            `next-state` function
            ...

        Flesh Out the Code

        Final Thoughts
            Mostly top down & a little bottom-up?
            Re: final thoughts of previous article


