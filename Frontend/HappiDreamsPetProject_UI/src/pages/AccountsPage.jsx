import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import AddressPage from './AddressPage';
import ChangePassword from './ChangePassword';

function AccountsPage() {
  const [currentTab, setCurrentTab] = useState('password');
  const [renderContent, setRenderContent] = useState();

  const handleTabChange = (tab) => {
    setCurrentTab(tab);
  };

  useEffect(() => {
    if (currentTab === 'password') {
        setRenderContent(
            <div className='flex justify-center flex-grow'><ChangePassword /> </div>

        );
      } else if (currentTab === 'address') {
        setRenderContent(
          <AddressPage isCheckOutShown={false} fromPage={"Accounts"} isSaveShown={true} isDefaultNeedsToBeGiven={true} />
        );
      }
  }, [currentTab])


  return (
    <div className='flex flex-row'>
    <Sidebar handleTabChange={handleTabChange} />
    {renderContent}
    </div>
  );
}

export default AccountsPage;
