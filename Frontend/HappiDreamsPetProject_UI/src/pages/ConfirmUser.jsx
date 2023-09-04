import LoginInput from "../components/LoginData/LoginInput";
import { confirmUserFields } from "../components/LoginData/LoginFormFields";
import LoginFormAction from "../components/LoginData/LoginFormAction";
import { useEffect, useState } from "react";
import { toast } from 'react-toastify';
import { useNavigate } from "react-router-dom";
import UINotification from "../components/UINotification";
import { confirmUser } from "../services/ApiClient";

function ConfirmUser({isUserAuthenticated, userId, afterConfirm}){
    const [fields, setFields] = useState([]);
    const [confirmOTPState,setConfirmOTPState]=useState({});
    const navigate = useNavigate();

    const handleChange=(e)=>{
        setConfirmOTPState({...confirmOTPState,[e.target.id]:e.target.value})
    }

    const handleSubmit=(e)=>{
        e.preventDefault();
        confirmUserViaAPI();
    }

  const confirmUserViaAPI = async () => {
    const response = await confirmUser(userId, confirmOTPState.confirmUserOTP);
    if (response.isSuccess) {
      UINotification({ message: "User Confirmed Successfully", type: "Success" });

      if (isUserAuthenticated) {

        if (typeof afterConfirm === "function") {
          await afterConfirm();
        }
        navigate("/home");
      } else {
        navigate("/login");
      }

    } else {
      UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
    }
  }

    useEffect(() => {
            let dummyfields=confirmUserFields;
            let fieldsState = {};
            dummyfields.forEach(field=>fieldsState[field.id]='');
            setConfirmOTPState(fieldsState);
            setFields(dummyfields);
    }, []);

    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
return(
    <div className={`mt-5 ${isMobileView() ? '' : 'loginMargin' }`}>
    <h2 className="sm:mt-0 mt-6 text-center text-3xl sm:text-4xl font-extrabold text-gray-900">
        Enter OTP To Confirm User
    </h2>

   <form className="mt-2 space-y-1 sm:mt-8 sm:space-y-4" onSubmit={handleSubmit}>
   <div className="flex flex-col">
        {
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        value={confirmOTPState[field.id]}
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
    </div>

    <LoginFormAction handleSubmit={handleSubmit} text="Next"/>

  </form>


  </div>

)
}

export default ConfirmUser;