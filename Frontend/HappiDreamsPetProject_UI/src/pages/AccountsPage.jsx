import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import AddressPage from './AddressPage';
import ChangePassword from './ChangePassword';
import { useNavigate } from 'react-router-dom';

function AccountsPage({selectedTab}) {
  const [renderContent, setRenderContent] = useState();
  const navigate = useNavigate();
  selectedTab = selectedTab === null || selectedTab === undefined ? undefined : selectedTab;
  useEffect(() => {
  switch(selectedTab){
    case "Password":
      setRenderContent(
        <div className='flex justify-center flex-grow'><ChangePassword /> </div>
    );
    break;
    case "Address":
      setRenderContent(
        <AddressPage isCheckOutShown={false} fromPage={"Accounts"} isSaveShown={false} isDefaultNeedsToBeGiven={true} />
      );
      break;
  }
  }, [selectedTab])

  const tabs = [
    {
      label: "Password",
      handleTabChange: function(){
        console.log("click");
        navigate("/accounts/password");
      }
    },
    {
      label: "Address",
      handleTabChange: function(){
        navigate("/accounts/address");
      }
    }
  ]

  return (
    <div className='flex flex-row'>
    <Sidebar tabs={tabs} activeTabLabel={selectedTab} />
    {renderContent}
    </div>
  );
}

export default AccountsPage;
