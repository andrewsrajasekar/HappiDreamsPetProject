import { toast } from 'react-toastify';

function UINotification({message, type, persistWithOtherMessage}){
    const showToast = () => {
        if(persistWithOtherMessage === undefined || (persistWithOtherMessage !== undefined && !persistWithOtherMessage)){
            toast.dismiss();
        }
        switch (type.toLowerCase()) {
          case 'success':
            toast.success(message, {
                position: toast.POSITION.TOP_CENTER,
                hideProgressBar: true,
                pauseOnHover: false
            });
            break;
          case 'error':
            toast.error(message, {
                position: toast.POSITION.TOP_CENTER,
                hideProgressBar: true,
                pauseOnHover: false
            });
            break;
          case 'warning':
            toast.warning(message, {
                position: toast.POSITION.TOP_CENTER,
                hideProgressBar: true,
                pauseOnHover: false
            });
            break;
          default:
            toast.info(message, {
                position: toast.POSITION.TOP_CENTER,
                hideProgressBar: true
            });
            break;
        }
      };
    
      return (
        <>
          {showToast()}
        </>
      );
}

export default UINotification;