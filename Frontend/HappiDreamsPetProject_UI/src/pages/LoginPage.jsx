import LoginHeader from "../components/LoginData/LoginHeader"
import Login from "../components/Login"

export default function LoginPage(){
    return(
        <>
             <LoginHeader
                heading="Login to your account"
                paragraph="Don't have an account yet? "
                linkName="Signup"
                linkUrl="/signup"
                />
            <Login/>
        </>
    )
}