const loginFields=[
    {
        labelText:"Email address",
        labelFor:"email-address",
        id:"email-address",
        name:"email",
        type:"email",
        autoComplete:"email",
        isRequired:true,
        placeholder:"Email address"   
    },
    {
        labelText:"Password",
        labelFor:"password",
        id:"password",
        name:"password",
        type:"password",
        autoComplete:"current-password",
        isRequired:true,
        placeholder:"Password"   
    }
]

const forgotPasswordEmailField=[
    {
        labelText:"Email address",
        labelFor:"email-address",
        id:"email-address",
        name:"email",
        type:"email",
        autoComplete:"email",
        isRequired:true,
        placeholder:"Email address"   
    }
]

// const forgotPasswordOTPField=[
//     {
//         labelText:"OTP Password",
//         labelFor:"otp-password",
//         id:"otp-password",
//         name:"otppassword",
//         type:"text",
//         autoComplete:"otppassword",
//         isRequired:true,
//         placeholder:"OTP Password"   
//     }
// ]

const forgotPasswordPasswordFields=[
    {
        labelText:"OTP Password",
        labelFor:"otp-password",
        id:"otp-password",
        name:"otppassword",
        type:"text",
        autoComplete:"otppassword",
        isRequired:true,
        placeholder:"OTP Password"   
    },
    {
        labelText:"Password",
        labelFor:"password",
        id:"password",
        name:"password",
        type:"password",
        autoComplete:"current-password",
        isRequired:true,
        placeholder:"Password"   
    },
    {
        labelText:"Confirm Password",
        labelFor:"confirm-password",
        id:"confirm-password",
        name:"confirm-password",
        type:"password",
        autoComplete:"confirm-password",
        isRequired:true,
        placeholder:"Confirm Password"   
    }
]

const changePasswordFields=[
    {
        labelText:"Old Password",
        labelFor:"oldpassword",
        id:"oldpassword",
        name:"oldpassword",
        type:"password",
        autoComplete:"old-password",
        isRequired:true,
        placeholder:"Old Password"   
    },
    {
        labelText:"New Password",
        labelFor:"newpassword",
        id:"newpassword",
        name:"newpassword",
        type:"password",
        autoComplete:"new-password",
        isRequired:true,
        placeholder:"New Password"   
    },
    {
        labelText:"Confirm Password",
        labelFor:"confirm-password",
        id:"confirm-password",
        name:"confirm-password",
        type:"password",
        autoComplete:"confirm-password",
        isRequired:true,
        placeholder:"Confirm Password"   
    }
]

const confirmUserFields=[
    {
        labelText:"OTP",
        labelFor:"OTP",
        id:"confirmUserOTP",
        name:"confirmUserOTP",
        type:"password",
        autoComplete:"otp-password",
        isRequired:true,
        placeholder:"OTP"   
    }
]

const signupFields=[
    {
        labelText:"Username",
        labelFor:"username",
        id:"username",
        name:"username",
        type:"text",
        autoComplete:"username",
        isRequired:true,
        placeholder:"Username"   
    },
    {
        labelText:"Email address",
        labelFor:"email-address",
        id:"email",
        name:"email",
        type:"email",
        autoComplete:"email",
        isRequired:true,
        placeholder:"Email address"   
    },
    {
        labelText:"Mobile Number",
        labelFor:"mobile-number",
        id:"mobile-number",
        name:"mobilenumber",
        type:"text",
        minLength:10,
        maxLength:10,
        autoComplete:"mobilenumber",
        isRequired:true,
        placeholder:"Mobile Number"   
    },
    {
        labelText:"Password",
        labelFor:"password",
        id:"password",
        name:"password",
        type:"password",
        autoComplete:"current-password",
        isRequired:true,
        placeholder:"Password"   
    },
    {
        labelText:"Confirm Password",
        labelFor:"confirm-password",
        id:"confirm-password",
        name:"confirm-password",
        type:"password",
        autoComplete:"confirm-password",
        isRequired:true,
        placeholder:"Confirm Password"   
    }
]

export {loginFields, forgotPasswordEmailField, forgotPasswordPasswordFields, changePasswordFields, confirmUserFields, signupFields}