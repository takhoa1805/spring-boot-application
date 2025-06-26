const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
export const URL = `${protocol}://${window.location.hostname}:5000/ws`;


// export default socket;
