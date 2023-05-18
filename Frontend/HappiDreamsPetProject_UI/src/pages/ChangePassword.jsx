import LoginInput from "../components/LoginData/LoginInput";
import { changePasswordFields } from "../components/LoginData/LoginFormFields";
import LoginFormAction from "../components/LoginData/LoginFormAction";
import { useEffect, useState } from "react";
import { toast } from 'react-toastify';
import { useNavigate } from "react-router-dom";

function ChangePassword(){
    const [fields, setFields] = useState([]);
    const [changePasswordState,setChangePasswordState]=useState({});
    const navigate = useNavigate();

    const handleChange=(e)=>{
        setChangePasswordState({...changePasswordState,[e.target.id]:e.target.value})
    }

    const handleSubmit=(e)=>{
        e.preventDefault();
        //authenticateUser();
        toast.success('Password Changed Successfully', {
            position: toast.POSITION.TOP_CENTER
          });
    }

    useEffect(() => {
            let dummyfields=changePasswordFields;
            let fieldsState = {};
            dummyfields.forEach(field=>fieldsState[field.id]='');
            setChangePasswordState(fieldsState);
            setFields(dummyfields);
    }, []);

    const isMobileView = () => {
        return window.innerWidth < 1024;
      }
return(
    <div className="mt-5">
    <h2 className="sm:mt-0 mt-6 text-center text-3xl sm:text-4xl font-extrabold text-gray-900">
        Change Your Password
    </h2>

   <form className="mt-2 space-y-1 sm:mt-8 sm:space-y-4" onSubmit={handleSubmit}>
   <div className="flex flex-col">
        {
            fields.map(field=>
                    <LoginInput
                        key={field.id}
                        handleChange={handleChange}
                        value={changePasswordState[field.id]}
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

export default ChangePassword;