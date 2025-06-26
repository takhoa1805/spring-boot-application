// import { io } from 'socket.io-client';

// "undefined" means the URL will be computed from the `window.location` object
const protocol = window.location.protocol === 'https:' ? 'https' : 'http';
export const URL =`${protocol}://${window.location.hostname}:5001`;
// const URL =`http://localhost:5001`;


export const sendMessage = ({message,event,socket})=>{
    socket.emit(event,message);
    console.log("message sent",event,message);
}

// export const socket = io(URL);
