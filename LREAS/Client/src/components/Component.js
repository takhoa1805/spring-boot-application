import React from "react";

export default function Component() {
    console.log('process.env.ENV',process.env.REACT_APP_ENV);
    return (
        <div>
            <h1>Hello world from LREAS {process.env.REACT_APP_ENV === 'development' ? 'Development' : 'Production'} environment
            </h1>
        </div>
    )
}