import LoginHeader from "../components/LoginData/LoginHeader";
import Signup from "../components/Signup";

export default function SignupPage(){
    return(
        <>
            <LoginHeader
              heading="Signup to create an account"
              paragraph="Already have an account? "
              linkName="Login"
              linkUrl="/login"
            />
            <Signup/>
        </>
    )
}