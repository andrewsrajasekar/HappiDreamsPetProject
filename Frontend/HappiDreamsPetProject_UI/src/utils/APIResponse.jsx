function APIResponse(){
    
}

export function createAPIResponse() {
    return {
        isSuccess: false,
        statusCode: 200,
        isAuthTokenFailure: false,
        successResponse: {
            data: {}
        },
        failureResponse: {
            error_code: "",
            errors: {},
            message: ""
        }
    };
}

export default APIResponse;
