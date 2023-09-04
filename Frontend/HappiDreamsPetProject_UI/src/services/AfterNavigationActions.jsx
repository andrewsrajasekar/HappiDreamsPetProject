export const getIsReset = (location) => {
    if (location !== null || location !== undefined) {
        if (location.hasOwnProperty("state") && location.state !== null && location.state.hasOwnProperty("resetPage")) {
            return location.state.resetPage;
        }
    }
    return false;
}
