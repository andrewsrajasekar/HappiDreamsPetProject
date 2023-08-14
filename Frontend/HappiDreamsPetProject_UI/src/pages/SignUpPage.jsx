import { useState } from "react";
import LoginHeader from "../components/LoginData/LoginHeader";
import Signup from "../components/Signup";
import ConfirmUser from "./ConfirmUser";

export default function SignupPage() {
    const [isConfirmUser, setIsConfirmUser] = useState(false);
    const [userId, setUserId] = useState();
    return (
        <>

            {!isConfirmUser &&
                <>
                    <LoginHeader
                        heading="Signup to create an account"
                        paragraph="Already have an account? "
                        linkName="Login"
                        linkUrl="/login"
                    />
                    <Signup onSuccessfulSignUp={(id) => {setIsConfirmUser(true); setUserId(id);}} />
                </>
            }
            {isConfirmUser &&
                <>
                    <div>
                        <ConfirmUser isUserAuthenticated={false} userId={userId} />
                    </div>
                </>
            }
        </>
    )
}