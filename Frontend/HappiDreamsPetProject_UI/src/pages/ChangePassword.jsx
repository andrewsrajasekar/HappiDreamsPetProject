import LoginInput from "../components/LoginData/LoginInput";
import { changePasswordFields } from "../components/LoginData/LoginFormFields";
import LoginFormAction from "../components/LoginData/LoginFormAction";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { changePassword } from "../services/ApiClient";
import UINotification from '../components/UINotification.jsx';

const fields = changePasswordFields;
let fieldsState = {};

fields.forEach(field => fieldsState[field.id] = '');

function ChangePassword(){
    const [errors, setErrors] = useState({});
    const [isSubmitButtonDisabled, setIsSubmitButtonDisabled] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);
    const [changePasswordState,setChangePasswordState]=useState(fieldsState);
    const navigate = useNavigate();

    const validateForm = () => {

        const isOldPasswordValid = changePasswordState.oldpassword.trim() !== "" && changePasswordState.oldpassword.trim().length >= 8;
        const isNewPasswordValid = changePasswordState.newpassword.trim() !== "" && changePasswordState.newpassword.trim().length >= 8;
        const isConfirmPasswordValid = changePasswordState["confirm-password"].trim() !== "" && changePasswordState["confirm-password"].trim().length >= 8;

        setIsFormValid(isNewPasswordValid && isOldPasswordValid && isConfirmPasswordValid);
    };

    useEffect(() => {
        validateForm();
      }, [changePasswordState]);

    const handleChange=(e)=>{
        setChangePasswordState({...changePasswordState,[e.target.id]:e.target.value});
        setErrors({ ...errors, [e.target.id]: '' });
    }

    const handleBlur = (event) => {
        event.preventDefault();
        let id = event.target.id;
        let value = event.target.value;
        if (id === "oldpassword" && value.trim().length > 0) {
            if (value.trim().length < 8) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    [id]: 'The Password must be exactly 8 characters long'
                }))
            }
            if (value === changePasswordState["newpassword"].trim()) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    ["newpassword"]: 'New Password must not be same as old Password'
                }))
            } else {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    ["newpassword"]: ''
                }))
            }
        }
        if (id === "newpassword" && value.trim().length > 0) {
            if (value.trim().length < 8) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    [id]: 'The Password must be exactly 8 characters long'
                }))
            }
            if (value === changePasswordState["oldpassword"].trim()) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    "newpassword": 'New Password must not be same as old Password',
                }))
            } else {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    ["newpassword"]: ''
                }))
            }
            if (changePasswordState["confirm-password"].trim().length > 0  && changePasswordState["confirm-password"].trim() !== value) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    ["confirm-password"]: 'Password does not match'
                }))
            } else {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    "confirm-password": '',
                }))
            }
        }
        if (id === "confirm-password" && value.trim().length > 0) {
            if (changePasswordState["newpassword"].trim() !== value) {
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

    const handleSubmit=(e)=>{
        e.preventDefault();
        let newErrors = {};
    let hasErrors = false;

    // Perform validation for each field
    fields.forEach((field) => {
      const { id, isRequired } = field;
      const value = changePasswordState[id].trim();

      if (isRequired && value === '') {
        newErrors[id] = `${field.labelText} is required`;
        hasErrors = true;
      }
      if (id === "oldpassword" && changePasswordState["oldpassword"].trim().length < 8) {
        newErrors[id] = `The Password must be exactly 8 characters long`;
        hasErrors = true;
      }
      if (id === "newpassword" && changePasswordState["newpassword"].trim().length < 8) {
        newErrors[id] = `The Password must be exactly 8 characters long`;
        hasErrors = true;
      }
      if (id === "confirm-password" && changePasswordState["newpassword"].trim() !== value) {
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
        changePasswordViaAPI();
    }

    const changePasswordViaAPI = async () => {
        setIsSubmitButtonDisabled(true);
        const response = await changePassword(changePasswordState.oldpassword, changePasswordState.newpassword);
        if (response.isSuccess) {   
            UINotification({message: "Password Changed Successfully", type: "Success"});
          }else{
            let knownErrors = false;
            if(response.statusCode === 400){
              let failureResponse = response.failureResponse;
              if(failureResponse.error_code === "INVALID_DATA" && failureResponse.errors.hasOwnProperty("field") && failureResponse.errors.field === "old_password"){
                let newErrors = {};
                newErrors["oldpassword"] = `Invalid Old Password`;
                setErrors(newErrors);
                knownErrors = true;
              }else if(failureResponse.error_code === "INVALID_DATA" && failureResponse.errors.hasOwnProperty("field") && failureResponse.errors.field === "new_password"){
                if(failureResponse.errors.message && failureResponse.errors.message === "New Password cannot be same as Old Password"){
                    let newErrors = {};
                    newErrors["newpassword"] = `New Password must not be same as old Password`;
                    setErrors(newErrors);
                    knownErrors = true;
                }
              }
            }
            if(!knownErrors){
              UINotification({message: "Issue Occured, Kindly try again later.", type: "Error"});
            }    
          }
          setIsSubmitButtonDisabled(false);
    }

    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
return(
    <div className="mt-5">
    <h2 className="sm:mt-0 mt-6 text-center text-3xl sm:text-4xl font-extrabold text-gray-900">
        Change Your Password
    </h2>

   <form className="mt-2 space-y-1 sm:mt-8 sm:space-y-4" onSubmit={handleSubmit}>
   <div className="">
        {
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        handleBlur={handleBlur}
                        value={changePasswordState[field.id]}
                        labelText={field.labelText}
                        labelFor={field.labelFor}
                        id={field.id}
                        name={field.name}
                        type={field.type}
                        isRequired={field.isRequired}
                        placeholder={field.placeholder}
                        errors={errors}
                        
                />
            
            )
        }
    </div>

    <LoginFormAction handleSubmit={handleSubmit} isSubmitButtonDisabled={!isFormValid || isSubmitButtonDisabled} text="Save"/>

  </form>


  </div>

)
}

export default ChangePassword;