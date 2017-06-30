import React from 'react';
import ReactDOM from 'react-dom';
import { connect, Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import { Map } from 'immutable';
import fetch from 'isomorphic-fetch';
import thunkMiddleware from 'redux-thunk';

// *************
// ** Actions **
// *************

const SET_FIRST_NAME = 'SET_FIRST_NAME';
function setFirstName(firstName) {
    return {type: SET_FIRST_NAME, value: firstName};
}

const SET_LAST_NAME = 'SET_LAST_NAME';
function setLastName(lastName) {
    return {type: SET_LAST_NAME, value: lastName};
}

const SET_PASSWORD = 'SET_PASSWORD';
function setPassword(password) {
    return {type: SET_PASSWORD, value: password};
}

const SET_CONFIRM_PASSWORD = 'SET_CONFIRM_PASSWORD';
function setConfirmPassword(confirmPassword) {
    return {type: SET_CONFIRM_PASSWORD, value: confirmPassword};
}

const START_SUBMIT = 'START_SUBMIT';
function startSubmit() {
    return {type: START_SUBMIT};
}

const SUBMIT_SUCCESS = 'SUBMIT_SUCCESS';
function submitSuccess() {
    return {type: SUBMIT_SUCCESS};
}

const SUBMIT_FAILURE = 'SUBMIT_FAILURE';
function submitFailure(error) {
    return {type: SUBMIT_FAILURE, value: error};
}

function validate(props) {
    let blank = function (s) { return s.trim() == ""; };

    if (blank(props.firstName)) {
        return "first name blank";
    } else if (blank(props.lastName)) {
        return "last name blank";
    } else if (blank(props.password)) {
        return "password must not be blank";
    } else if (props.password.length < 8) {
        return "password must be 8 characters";
    } else if (props.password != props.confirmPassword) {
        return "passwords must match";
    }
}

function submit(props) {

    return function(dispatch) {
        dispatch(startSubmit());

        const error = validate(props);

        if (error != null) {
            dispatch(submitFailure(error));
        } else {
            // TODO Mock a form submission, but use an actual ajax call.
            return fetch(
                    "http://www.google.com",
                    {mode: 'no-cors'}
                    ).then(
                        function (response) {
                            dispatch(submitSuccess());
                        },
                        function (err) {
                            //dispatch(submitError());
                        });
        }

    };
}

// **************
// ** Reducers **
// **************

// NOTE The reducers are like reframe's event handling functions. They're
// supposed to be pure -- no side effects, random numbers, ajax, etc.
function rootReducer(state, action) {
    console.log("reducing " + action.type + " " + action.value);
    switch (action.type) {
        case SET_FIRST_NAME:
            const error = state.get("error");
            let newState = state.set("firstName", action.value);
            if (error && error.startsWith("first")) {
                newState = newState
                    .set("error", null)
                    .set("submitEnabled", true);
            }
            return newState;
        case SET_LAST_NAME:
            // TODO see SET_FIRST_NAME
            return state.set("lastName", action.value);
        case SET_PASSWORD:
            // TODO see SET_FIRST_NAME
            return state.set("password", action.value);
        case SET_CONFIRM_PASSWORD:
            // TODO see SET_FIRST_NAME
            return state.set("confirmPassword", action.value);
        case START_SUBMIT:
            return state.set("submitEnabled", false);
        case SUBMIT_SUCCESS:
            return state.set("submitEnabled", true);
        case SUBMIT_FAILURE:
            return state
                .set("submitEnabled", false)
                .set("error", action.value);
        default:
            return Map({
                firstName: "",
                lastName: "",
                password: "",
                confirmPassword: "",
                submitEnabled: true,
                error: null
            });
    }
}

// NOTE Is there an analog to re-frame's subscriptions? These are just props.
function mapStateToProps(state) {
    return state.toJS();
}

// NOTE Doesn't seem to be any re-frame analog here. This is just plumbing
// code. I assume the heavy lifting should be done in the reducers. This is
// just a means of closing over `dispatch`. In re-frame, you can call
// dispatch directly from the view code.
function mapDispatchToProps(dispatch) {
    return {
        onFirstNameChange: function(e) {
            dispatch(setFirstName(e.target.value));
        },
        onLastNameChange: function(e) {
            dispatch(setLastName(e.target.value));
        },
        onPasswordChange: function(e) {
            dispatch(setPassword(e.target.value));
        },
        onConfirmPasswordChange: function(e) {
            dispatch(setConfirmPassword(e.target.value));
        },
        onSubmit: function(e, props) {
            e.preventDefault();
            dispatch(submit(props));
        }
    };
}

// ***************
// ** Rendering **
// ***************

function RegForm(props) {
    return (<form onSubmit={function (e) {props.onSubmit(e, props);}}>
        <div style={{color: "red"}}>{props.error}</div>
        First Name:
        <input onChange={props.onFirstNameChange} value={props.firstName}/>
        <br/>
        Last Name:
        <input onChange={props.onLastNameChange} value={props.lastName}/>
        <br/>
        Password:
        <input onChange={props.onPasswordChange} value={props.password}/>
        <br/>
        Confirm Password:
        <input onChange={props.onConfirmPasswordChange} value={props.confirmPassword}/>
        <br/>
        <input type="submit" value="Register" disabled={!props.submitEnabled}/>
    </form>);
}

// **********
// ** Main **
// **********

var ReduxRegForm = connect(mapStateToProps, mapDispatchToProps)(RegForm);

var store = createStore(
        rootReducer,
        applyMiddleware(
            thunkMiddleware));

ReactDOM.render(
        <Provider store={store}>
            <ReduxRegForm/>
        </Provider>,
        document.getElementById('root'));
