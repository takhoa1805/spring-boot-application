module.exports = {
  apps: [
    {
      name: "lreas-react-app",
      script: "npm",
      args: "run serve",
      env: {
        REACT_APP_ENV: "production",
        PUBLIC_URL: "http://lreas.takhoa.site",
        NODE_ENV: "production"
      }
    }
  ]
};
