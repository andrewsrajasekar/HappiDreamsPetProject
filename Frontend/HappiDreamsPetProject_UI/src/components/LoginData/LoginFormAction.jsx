export default function LoginFormAction({
    handleSubmit,
    type='Button',
    action='submit',
    text,
    isSubmitButtonDisabled
}){
    return(
        <>
        {
            type==='Button' ?
            <button
                type={action}
                className={`group disabled:opacity-25 disabled:cursor-not-allowed sm:mb-3 relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-900 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 mt-10 sm:mt-0`}
                onSubmit={isSubmitButtonDisabled ? null : handleSubmit}
                disabled={isSubmitButtonDisabled}
            >

                {text}
            </button>
            :
            <></>
        }
        </>
    )
}