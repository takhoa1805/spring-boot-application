exports.getPermissions = async ({ token, mongoId, readPermission, writePermission }) => {
    try {
        const FILE_MANAGEMENT_VERIFICATION_URL = `http://file-management-service:2005/resources/documents/${mongoId}/permissions`;
        const headers = {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/x-www-form-urlencoded'
        };

        const res = await fetch(FILE_MANAGEMENT_VERIFICATION_URL, {
            method: 'GET',
            headers: headers
        });

        const json = await res.json();
        console.log("Permissions:", json);

        if (json?.success && json?.readPermission === readPermission) {
            if (writePermission) {
                return json?.writePermission === writePermission;
            }
            return  true;
        }
        return false;
    } catch (error) {
        console.error("Error getting permissions:", error);
        return false;
    }
};
