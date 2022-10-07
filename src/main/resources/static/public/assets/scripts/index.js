const {createApp} = Vue

createApp({
  data(){
    return {
        isLoaded: false,
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        passwordConfirm: "",
        error: false,
        emailError: false,
        passwordError: false,
        upperCaseError: false,
        numberError: false,
        lengthError: false,
        hide: true,
        validate: false,

    }
  },
  created(){
  },
  mounted(){
    document.onreadystatechange = () => {
      if (document.readyState = "complete"){
        this.isLoaded = true
      }
    }
  },
  methods: {
    login(){

        axios.post('/api/login',"email=" + this.email + "&password=" + this.password,{headers : {'Content-Type':'application/x-www-form-urlencoded'}})
        .then(response => {
            if (this.email == "admin@gmail.com"){
              window.location.href = "/admin/manager.html"
            }else {
              window.location.href = "/web/accounts.html"
            }
        })
        .catch(error => {
          console.log(error)
          this.error = true
        })
    },
    register(){
      if (this.passwordValidator() && this.validator())
      {
        axios.post('/api/clients',"firstName=" + this.capitalize(this.firstName) + "&lastName=" + this.capitalize(this.lastName) + "&email=" + this.email + "&password=" + this.password,{headers:{'Content-Type':'application/x-www-form-urlencoded'}})
        .then(response => {
          this.login()
        })
        .catch(error => {
          if (error.response.data == "Email already in use"){
            this.emailError = true
          }
        })
      }
    },
    validator(){
      let upperCaseLetters = /[A-Z]/g;
      let numbers = /[0-9]/g;
      if (!this.password.match(upperCaseLetters)){
        this.upperCaseError = true
      }
      else{
        this.upperCaseError = false
      }
      if (!this.password.match(numbers)){
        this.numberError = true
      }
      else {
        this.numberError = false
      }
      if (this.password.length < 8){
        this.lengthError = true
      }
      else {
        this.lengthError = false
      }

      return !(this.lowerCaseError || this.numberError || this.lengthError)
    },
    isValidate(){
      if (this.validate){
        this.passwordValidator()
      }
    },
    passwordValidator(){
      this.validate = true
      if (this.password == this.passwordConfirm){
        this.passwordError = false
      }else {
        this.passwordError = true
      }
      return !this.passwordError
    },
    capitalize(str){
      const lower = str.toLowerCase()
      return str.charAt(0).toUpperCase() + lower.slice(1)
    },
    showPass(){
      if (this.hide){
        this.hide = false
      }else {
        this.hide = true
      }
    }
  }
}).mount("#app")