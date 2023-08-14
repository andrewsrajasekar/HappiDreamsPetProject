import LoginInput from "../components/LoginData/LoginInput";
import { forgotPasswordEmailField, forgotPasswordPasswordFields } from "../components/LoginData/LoginFormFields";
import LoginFormAction from "../components/LoginData/LoginFormAction";
import { useEffect, useState } from "react";
import { toast } from 'react-toastify';
import { useNavigate } from "react-router-dom";
import UINotification from "../components/UINotification";
import { forgotPasswordOTPTrigger, validateAndUpdateForgotPassword } from "../services/ApiClient";

function ForgotPassword(){
    const [fields, setFields] = useState([]);
    const [isSubmitButtonDisabled, setIsSubmitButtonDisabled] = useState(false);
    const [isEmailFormValid, setIsEmailFormValid] = useState(false);
    const [isPasswordFormValid, setIsPasswordFormValid] = useState(false);
    const [forgotPasswordEmailState,setForgotPasswordEmailState]=useState({});
    const [forgotPasswordPasswordState,setForgotPasswordPasswordState]=useState({});
    let [isEmailPage, setIsEmailPage] = useState(true);
    let [isPasswordPage, setIsPasswordPage] = useState(false);
    const navigate = useNavigate();

    const handleChange=(e)=>{
        if(isEmailPage){
            setForgotPasswordEmailState({...forgotPasswordEmailState,[e.target.id]:e.target.value})
        } else if(isPasswordPage){
            setForgotPasswordPasswordState({...forgotPasswordPasswordState,[e.target.id]:e.target.value})
        }

    }

    const validateForm = () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        const isEmailValid = emailRegex.test(forgotPasswordEmailState["email-address"]);

        setIsEmailFormValid(isEmailValid);
    };

    const validatePasswords = () => {
        const isOTPValid = forgotPasswordPasswordState.hasOwnProperty("otp-password") && forgotPasswordPasswordState["otp-password"].trim() !== "";
        const isPasswordValid = forgotPasswordPasswordState.hasOwnProperty("password") && forgotPasswordPasswordState["password"].trim() !== "";
        const isReEntryPasswordValid = forgotPasswordPasswordState.hasOwnProperty("confirm-password") && forgotPasswordPasswordState["confirm-password"].trim() !== "";

        setIsPasswordFormValid(isOTPValid && isPasswordValid && isReEntryPasswordValid);
    };

    useEffect(() => {
        validateForm();
    }, [forgotPasswordEmailState]);

    useEffect(() => {
        validatePasswords();
    }, [forgotPasswordPasswordState]);

    useEffect(() => {
        if(isEmailPage && isEmailFormValid){
            setIsSubmitButtonDisabled(false);
        }else if(isPasswordPage && isPasswordFormValid){
            setIsSubmitButtonDisabled(false);
        }else{
            setIsSubmitButtonDisabled(true);
        }
        
    }, [isEmailFormValid, isPasswordFormValid, isEmailPage, isPasswordPage])

    const handleSubmit= async (e) => {
        e.preventDefault();
        if(isEmailPage){
            await triggerOTPEmail();
            setIsPasswordPage(true);
            setIsEmailPage(false);
        } else{
            await changePassword();
        }
    }

    const triggerOTPEmail = async () => {
        setIsSubmitButtonDisabled(true);
        const response = await forgotPasswordOTPTrigger(forgotPasswordEmailState["email-address"]);
        if (response.isSuccess) {
            UINotification({ message: "Email With OTP Sent", type: "Success" });
        } else {
            UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        }
        setIsSubmitButtonDisabled(false);
    }

    const changePassword = async () => {
        setIsSubmitButtonDisabled(true);
        const response = await validateAndUpdateForgotPassword(forgotPasswordEmailState["email-address"], forgotPasswordPasswordState["otp-password"], forgotPasswordPasswordState["password"]);
        if (response.isSuccess) {
            UINotification({ message: "Password Changed Successfully", type: "Success" });
            navigate("/login");
        } else {
            UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
        }
        setIsSubmitButtonDisabled(false);
    }

    useEffect(() => {
        if(isEmailPage){
            
            let dummyfields=forgotPasswordEmailField;
            let fieldsState = {};
            dummyfields.forEach(field=>fieldsState[field.id]=''); 
            setForgotPasswordEmailState(fieldsState); 
            setFields(dummyfields);   
        }
    }, [isEmailPage]);
    useEffect(() => {
        if(isPasswordPage){
            
            let dummyfields=forgotPasswordPasswordFields;
            let fieldsState = {};
            dummyfields.forEach(field=>fieldsState[field.id]='');
            setForgotPasswordPasswordState(fieldsState);
            setFields(dummyfields);
        }
    }, [isPasswordPage]);

    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
return(
    <div>
    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
        Forgot Your Password
    </h2>

   <form className={` space-y-6 ${isMobileView() ? '' : 'loginMarginWithoutTopAndBottom' } mt-8`} onSubmit={handleSubmit}>
   {isEmailPage && <div className="-space-y-px">
        {
           
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        value={forgotPasswordEmailState[field.id]}
                        labelText={field.labelText}
                        labelFor={field.labelFor}
                        id={field.id}
                        name={field.name}
                        type={field.type}
                        isRequired={field.isRequired}
                        placeholder={field.placeholder}
                />
            
            )
        }
    </div>}

    {isPasswordPage && <div>
        {
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        value={forgotPasswordPasswordState[field.id]}
                        labelText={field.labelText}
                        labelFor={field.labelFor}
                        id={field.id}
                        name={field.name}
                        type={field.type}
                        isRequired={field.isRequired}
                        placeholder={field.placeholder}
                />
            
            )
        }
    </div>}

    <LoginFormAction isSubmitButtonDisabled={isSubmitButtonDisabled} handleSubmit={handleSubmit} text={isPasswordPage ? "Save" : "Next"}/>

  </form>


  </div>

)
}

export default ForgotPassword;