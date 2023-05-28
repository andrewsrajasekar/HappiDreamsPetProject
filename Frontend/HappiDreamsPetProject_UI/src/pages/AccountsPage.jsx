import React, { useState } from 'react';
import Sidebar from '../components/Sidebar';
import AddressPage from './AddressPage';
import ChangePassword from './ChangePassword';

function AccountsPage() {
  const [renderContent, setRenderContent] = useState();

  const tabs = [
    {
      label: "Password",
      handleTabChange: function(){
        setRenderContent(
          <div className='flex justify-center flex-grow'><ChangePassword /> </div>
      );
      }
    },
    {
      label: "Address",
      handleTabChange: function(){
        setRenderContent(
          <AddressPage isCheckOutShown={false} fromPage={"Accounts"} isSaveShown={true} isDefaultNeedsToBeGiven={true} />
        );
      }
    }
  ]

  return (
    <div className='flex flex-row'>
    <Sidebar tabs={tabs} />
    {renderContent}
    </div>
  );
}

export default AccountsPage;
