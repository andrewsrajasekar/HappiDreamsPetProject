import { useState } from 'react';
import { signupFields } from "./LoginData/LoginFormFields"
import LoginFormAction from "./LoginData/LoginFormAction";
import LoginInput from "./LoginData/LoginInput";

const fields=signupFields;
let fieldsState={};

fields.forEach(field => fieldsState[field.id]='');

export default function Signup(){
  const [signupState,setSignupState]=useState(fieldsState);

  const handleChange=(e)=> {
    if(e.target.id === "mobile-number"){
      let value = e.target.value;
      if (!(/^[0-9]*$/.test(value))) {
        return;
      }
    }
    setSignupState({...signupState,[e.target.id]:e.target.value})
  };

  const handleKeyPress = (event) => {
    if(event.target.id !== "mobile-number"){
      return;
    }
    const keyCode = event.keyCode || event.which;
    const keyValue = String.fromCharCode(keyCode);
    if (/[^0-9]/.test(keyValue)) {
      event.preventDefault();
    }
  };

  const handleSubmit=(e)=>{
    e.preventDefault();
    console.log(signupState)
    createAccount()
  }

  //handle Signup API Integration here
  const createAccount=()=>{

  }

  const isMobileView = () => {
    return window.innerWidth < 1024;
  }

    return(
        <form className={`mt-8 space-y-6 ${isMobileView() ? '' : 'loginMargin' }`} onSubmit={handleSubmit}>
        <div className="">
        {
                fields.map(field=>
                        <LoginInput
                            key={field.id}
                            handleChange={handleChange}
                            handleKeyPress={handleKeyPress}
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
                    />
                
                )
            }
          <LoginFormAction handleSubmit={handleSubmit} text="Signup" />
        </div>

         

      </form>
    )
}