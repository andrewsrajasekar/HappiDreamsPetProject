import LoginHeader from "../components/LoginData/LoginHeader"
import Login from "../components/Login"
import { useEffect, useState } from "react";
import {useLocation, useNavigate} from 'react-router-dom';
import ConfirmUser from "./ConfirmUser";
import { authenticateUser } from "../services/ApiClient";
import UINotification from "../components/UINotification";
import { logInUser } from "../services/SessionDetails";
import {getIsReset} from "../services/AfterNavigationActions";

export default function LoginPage() {
    const [isConfirmUser, setIsConfirmUser] = useState(false);
    const [isUserAuthenticated, setIsUserAuthenticated] = useState(false);
    const [userId, setUserId] = useState();
    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const location = useLocation();
    const navigate = useNavigate();
    useEffect(() => {
        if(getIsReset(location)){
            setIsConfirmUser(false);
            setIsUserAuthenticated(false);
        }
    })


    const afterConfirmFunction = async () => {
        try {
            const response = await authenticateUser(email, password);

            if (response.hasOwnProperty("data")) {
                logInUser(response.successResponse.data.data.id, response.successResponse.data.data.name, response.successResponse.data.data.email, response.successResponse.data.data.role, response.successResponse.data.data.role_id, response.successResponse.data.data.access_token, response.successResponse.data.data.expiration_date);
                UINotification({ message: "User Logged In Successfully", type: "Success" });
                navigate("/home");
            } else {
                UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
            }
        } catch (error) {
            UINotification({ message: "Issue Occured, Kindly try again later.", type: "Error" });
            console.error(error);
        }
    }
    return (
        <>
            {!isConfirmUser &&
                <>
                    <LoginHeader
                        heading="Login to your account"
                        paragraph="Don't have an account yet? "
                        linkName="Signup"
                        linkUrl="/signup"
                    />
                    <Login onConfirmUserFail={(id, email, password) => { setIsConfirmUser(true); setUserId(id); setIsUserAuthenticated(true); setEmail(email); setPassword(password); }} />
                </>
            }
            {isConfirmUser &&
                <>
                    <div>
                        <ConfirmUser isUserAuthenticated={isUserAuthenticated} userId={userId} afterConfirm={afterConfirmFunction} />
                    </div>
                </>
            }

        </>
    )
}