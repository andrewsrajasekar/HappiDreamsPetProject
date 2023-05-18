import { useState } from 'react';
import { loginFields } from "./LoginData/LoginFormFields";
import LoginFormAction from "./LoginData/LoginFormAction";
import LoginFormExtra from "./LoginData/LoginFormExtra";
import LoginInput from "./LoginData/LoginInput";

const fields=loginFields;
let fieldsState = {};
fields.forEach(field=>fieldsState[field.id]='');

export default function Login(){
    const [loginState,setLoginState]=useState(fieldsState);

    const handleChange=(e)=>{
        setLoginState({...loginState,[e.target.id]:e.target.value})
    }

    const handleSubmit=(e)=>{
        e.preventDefault();
        //authenticateUser();
    }

    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
    

    return(
        <form className={`mt-8 space-y-6 ${isMobileView() ? '' : 'loginMargin' }`} onSubmit={handleSubmit}>
        <div className="-space-y-px">
            {
                fields.map(field=>
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

        <LoginFormExtra/>
        <LoginFormAction handleSubmit={handleSubmit} text="Login"/>

      </form>
    )
}