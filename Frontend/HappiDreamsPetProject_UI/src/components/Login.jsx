import { useEffect, useState } from 'react';
import { loginFields } from "./LoginData/LoginFormFields";
import LoginFormAction from "./LoginData/LoginFormAction";
import LoginFormExtra from "./LoginData/LoginFormExtra";
import LoginInput from "./LoginData/LoginInput";
import { authenticateUser } from '../services/ApiClient';
import { logInUser } from '../services/SessionDetails';
import { useLocation, useNavigate } from 'react-router-dom';
import UINotification from './UINotification';
import { addKeyInActions } from '../services/AfterReloadActions';

const fields = loginFields;
let fieldsState = {};
fields.forEach(field => fieldsState[field.id] = '');

export default function Login({onConfirmUserFail}) {
    const [loginState, setLoginState] = useState(fieldsState);
    const navigate = useNavigate();
    const [isSubmitButtonDisabled, setIsSubmitButtonDisabled] = useState(false);
    const [isFormValid, setIsFormValid] = useState(false);

    const handleChange = (e) => {
        setLoginState({ ...loginState, [e.target.id]: e.target.value });
    }

    const validateForm = () => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        const isEmailValid = emailRegex.test(loginState["email-address"]);
        const isPasswordValid = loginState.password.trim() !== "";

        setIsFormValid(isEmailValid && isPasswordValid);
    };

    useEffect(() => {
        validateForm();
    }, [loginState]);

    const handleSubmit = (e) => {
        e.preventDefault();
        authenticateUserViaAPI(loginState["email-address"], loginState.password);
    }

    const authenticateUserViaAPI = async (email, password) => {  
            setIsSubmitButtonDisabled(true);
            const response = await authenticateUser(email, password);
            if (response.isSuccess) {   
                logInUser(response.successResponse.data.data.id, response.successResponse.data.data.name, response.successResponse.data.data.email, response.successResponse.data.data.role, response.successResponse.data.data.role_id, response.successResponse.data.data.access_token, response.successResponse.data.data.expiration_date);
                navigate("/home");
                addKeyInActions("showSuccessAfterLogin", true);
                window.location.reload();
            } else {
                let isNextStepDone = false;
                if(response.statusCode === 400){
                    if(response.failureResponse.error_code === "USER_NOT_CONFIRMED"){
                        if (typeof onConfirmUserFail === "function") {
                            isNextStepDone = true;
                            onConfirmUserFail(response.failureResponse.errors.user_id, email, password);
                            return;
                        }
                    }
                }else if(response.statusCode === 401){
                    if(response.failureResponse.error_code === "INVALID_CREDENTIALS"){
                        isNextStepDone = true;
                        UINotification({ message: "Invalid Email or Password", type: "Error" });
                    }
                }
                if(!isNextStepDone){
                    UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
                }    
            }
            setIsSubmitButtonDisabled(false);
    }

    const isMobileView = () => {
        return window.innerWidth < 1024;
    }


    return (
        <form className={`mt-8 space-y-6 ${isMobileView() ? '' : 'loginMargin'}`} onSubmit={handleSubmit}>
            <div className="-space-y-px">
                {
                    fields.map(field =>
                        <LoginInput
                            key={field.id}
                            handleChange={handleChange}
                            value={loginState[field.id]}
                            labelText={field.labelText}
                            labelFor={field.labelFor}
                            id={field.id}
                            minLength={field.minLength}
                            maxLength={field.maxLength}
                            name={field.name}
                            type={field.type}
                            isRequired={field.isRequired}
                            placeholder={field.placeholder}
                        />

                    )
                }
            </div>

            <LoginFormExtra />
            <LoginFormAction handleSubmit={handleSubmit} isSubmitButtonDisabled={!isFormValid || isSubmitButtonDisabled} text="Login" />

        </form>
    )
}