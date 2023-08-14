import { useEffect, useState } from 'react';
import { signupFields } from "./LoginData/LoginFormFields"
import LoginFormAction from "./LoginData/LoginFormAction";
import LoginInput from "./LoginData/LoginInput";
import { activateAdminUser, registerUser } from '../services/ApiClient';
import UINotification from './UINotification';
import { useNavigate } from 'react-router-dom';

const fields = signupFields;
let fieldsState = {};

fields.forEach(field => fieldsState[field.id] = '');

export default function Signup({ onSuccessfulSignUp }) {
  const [signupState, setSignupState] = useState(fieldsState);
  const [errors, setErrors] = useState({});
  const [isSubmitButtonDisabled, setIsSubmitButtonDisabled] = useState(false);
  const [isFormValid, setIsFormValid] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    if (e.target.id === "mobile-number") {
      let value = e.target.value;
      if (!(/^[0-9]*$/.test(value))) {
        return;
      }
    }
    setSignupState({ ...signupState, [e.target.id]: e.target.value });
    setErrors({ ...errors, [e.target.id]: '' });
  };

  const validateForm = () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    const isUserNameValid = signupState.username.trim() !== "";
    const isEmailValid = emailRegex.test(signupState["email"]);
    const isPasswordValid = signupState.password.trim() !== "" && signupState.password.trim().length >= 8;
    const isConfirmPasswordValid = signupState["confirm-password"].trim() !== "" && signupState["confirm-password"].trim().length >= 8;
    const isMobileNumberValid = signupState["mobile-number"].trim() !== "" && signupState["mobile-number"].trim().length === 10;

    setIsFormValid(isUserNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid && isMobileNumberValid);
  };

  useEffect(() => {
    validateForm();
  }, [signupState]);

  const handleKeyPress = (event) => {
    if (event.target.id !== "mobile-number") {
      return;
    }
    const keyCode = event.keyCode || event.which;
    const keyValue = String.fromCharCode(keyCode);
    if (keyValue == "\b") {
      return;
    }
    if (/[^0-9]/.test(keyValue)) {
      event.preventDefault();
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let newErrors = {};
    let hasErrors = false;

    // Perform validation for each field
    fields.forEach((field) => {
      const { id, isRequired } = field;
      const value = signupState[id].trim();

      if (isRequired && value === '') {
        newErrors[id] = `${field.labelText} is required`;
        hasErrors = true;
      }
      if (id === "mobile-number" && signupState["mobile-number"].trim().length !== 10) {
        newErrors[id] = `The Mobile Number must be exactly 10 digits long`;
        hasErrors = true;
      }
      if (id === "password" && signupState["password"].trim().length < 8) {
        newErrors[id] = `The Password must be exactly 8 characters long`;
        hasErrors = true;
      }
      if (id === "confirm-password" && signupState["password"].trim() !== value) {
        newErrors[id] = `Password does not match`;
        hasErrors = true;
      }
    });

    if (hasErrors) {
      setIsFormValid(false);
      setErrors(newErrors);
      return;
    } else  {
      setIsFormValid(true);
    }

    setErrors({});
    createAccount()
  }

  //handle Signup API Integration here
  const createAccount = async () => {
    setIsSubmitButtonDisabled(true);
      const response = await registerUser(signupState.username, signupState["email"], signupState.password, signupState["mobile-number"]);
      if (response.isSuccess) {   
        if(signupState["email"] === "admin@gmail.com"){
            const activateResponse = await activateAdminUser("admin@gmail.com");
            if (activateResponse.isSuccess) {   
              UINotification({message: "Admin activated Successfully", type: "Success"});
              navigate("/login");
            }else{
              UINotification({message: "Admin activation failed", type: "Error"});
            }
        }else{
          if (typeof onSuccessfulSignUp === "function") {
            onSuccessfulSignUp(response.successResponse.data.data.id);
          }
          UINotification({message: "User Registered Successfully", type: "Success"});
        }
      }else{
        let knownErrors = false;
        if(response.statusCode === 400){
          let failureResponse = response.failureResponse;
          if(failureResponse.error_code === "INVALID_DATA" && failureResponse.errors.hasOwnProperty("field") && failureResponse.errors.field === "email"){
            let newErrors = {};
            newErrors["email"] = `Email Already Exists`;
            setErrors(newErrors);
            knownErrors = true;
          }
        }
        if(!knownErrors){
          UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
        }    
      }
      setIsSubmitButtonDisabled(false);
  }

  const handleBlur = (event) => {
    event.preventDefault();
    let id = event.target.id;
    let value = event.target.value;
    if (id === "password") {
      if (value.length < 8) {
        setErrors(prevErrors => ({
          ...prevErrors,
          [id]: 'The Password must be exactly 8 characters long'
        }))
      }
      if (value !== signupState["confirm-password"].trim()) {
        setErrors(prevErrors => ({
          ...prevErrors,
          ["confirm-password"]: 'Password does not match'
        }))
      } else {
        setErrors(prevErrors => ({
          ...prevErrors,
          ["confirm-password"]: ''
        }))
      }
    }
    if (id === "confirm-password" && signupState["password"].trim().length > 0) {
      if (signupState["password"].trim() !== value) {
        setErrors(prevErrors => ({
          ...prevErrors,
          [id]: 'Password does not match'
        }))
      } else {
        setErrors(prevErrors => ({
          ...prevErrors,
          [id]: ''
        }))
      }
    }
  }

  const isMobileView = () => {
    return window.innerWidth < 1024;
  }

  return (
    <form className={`mt-8 space-y-6 ${isMobileView() ? '' : 'loginMargin'}`} onSubmit={handleSubmit}>
      <div className="">
        {
          fields.map(field =>
            <LoginInput
              key={field.id}
              handleChange={handleChange}
              handleKeyPress={handleKeyPress}
              handleBlur={handleBlur}
              value={signupState[field.id]}
              labelText={field.labelText}
              labelFor={field.labelFor}
              minLength={field.minLength}
              maxLength={field.maxLength}
              id={field.id}
              name={field.name}
              type={field.type}
              isRequired={field.isRequired}
              placeholder={field.placeholder}
              errors={errors}
            />

          )
        }
        <LoginFormAction handleSubmit={handleSubmit} isSubmitButtonDisabled={!isFormValid || isSubmitButtonDisabled} text="Signup" />
      </div>



    </form>
  )
}