import { useNavigate } from 'react-router-dom';

export default function LoginFormExtra(){

  const navigate = useNavigate();

  const goToForgotPassword = () => {
    navigate("/forgotpassword");
  }

    return(
        <div className="">
        {/* <div className="flex items-center">
          <input
            id="remember-me"
            name="remember-me"
            type="checkbox"
            className="h-4 w-4 text-purple-600 focus:ring-purple-500 border-gray-300 rounded"
          />
          <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
            Remember me
          </label>
        </div> */}

        <div className="text-right text-sm">
          <span onClick={goToForgotPassword} className="font-medium text-purple-600 hover:text-purple-500 cursor-pointer">
            Forgot your password?
          </span>
        </div>
      </div>

    )
}