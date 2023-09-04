export const addKeyInActions = (key, value) => {
    let actions = localStorage.getItem('doActionAfterReload');
    actions = actions === null || actions === undefined ? {} : JSON.parse(actions);
    actions[key] = value;
    localStorage.setItem('doActionAfterReload', JSON.stringify(actions));
}

export const getKeyInActions = (key) => {
    let actions = localStorage.getItem('doActionAfterReload');
    actions = actions === null || actions === undefined ? {} : JSON.parse(actions);
    if (actions.hasOwnProperty(key)) {
        return actions[key];
    }
    return null;
}

