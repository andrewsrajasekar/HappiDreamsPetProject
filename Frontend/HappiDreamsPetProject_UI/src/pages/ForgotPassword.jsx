import LoginInput from "../components/LoginData/LoginInput";
import { forgotPasswordEmailField, forgotPasswordOTPField, forgotPasswordPasswordFields } from "../components/LoginData/LoginFormFields";
import LoginFormAction from "../components/LoginData/LoginFormAction";
import { useEffect, useState } from "react";
import { toast } from 'react-toastify';
import { useNavigate } from "react-router-dom";

function ForgotPassword(){
    const [fields, setFields] = useState([]);
    const [forgotPasswordEmailState,setForgotPasswordEmailState]=useState({});
    const [forgotPasswordOTPState,setForgotPasswordOTPState]=useState({});
    const [forgotPasswordPasswordState,setForgotPasswordPasswordState]=useState({});
    let [isEmailPage, setIsEmailPage] = useState(true);
    let [isOtpPage, setIsOtpPage] = useState(false);
    let [isPasswordPage, setIsPasswordPage] = useState(false);
    const navigate = useNavigate();

    const handleChange=(e)=>{
        if(isEmailPage){
            setForgotPasswordEmailState({...forgotPasswordEmailState,[e.target.id]:e.target.value})
        }else if(isOtpPage){
            setForgotPasswordOTPState({...forgotPasswordOTPState,[e.target.id]:e.target.value})
        }else if(isPasswordPage){
            setForgotPasswordPasswordState({...forgotPasswordPasswordState,[e.target.id]:e.target.value})
        }

    }

    const handleSubmit=(e)=>{
        e.preventDefault();
        //authenticateUser();
        if(isEmailPage){
            setIsOtpPage(true);
            setIsEmailPage(false);
        }else if(isOtpPage){
            setIsPasswordPage(true);
            setIsOtpPage(false);
        }else{
            toast.success('Password Changed Successfully', {
                position: toast.POSITION.TOP_CENTER
              });
              navigate("/login");
        }
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
        if(isOtpPage){
            
            let dummyfields=forgotPasswordOTPField;
            let fieldsState = {};
            dummyfields.forEach(field=>fieldsState[field.id]='');
            setForgotPasswordOTPState(fieldsState);
            setFields(dummyfields);
        }
    }, [isOtpPage]);
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

   <form className={`mt-8 space-y-6 ${isMobileView() ? '' : 'loginMargin' }`} onSubmit={handleSubmit}>
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

    {isOtpPage && <div className="-space-y-px">
        {
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        value={forgotPasswordOTPState[field.id]}
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

    {isPasswordPage && <div className="-space-y-px">
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

    <LoginFormAction handleSubmit={handleSubmit} text="Next"/>

  </form>


  </div>

)
}

export default ForgotPassword;