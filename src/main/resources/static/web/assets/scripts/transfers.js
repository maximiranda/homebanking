

const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        accounts : [],
        transactions : [],
        accountType: "",
        moneyFormat : new Intl.NumberFormat('en-US',{  style: 'currency', currency: 'USD',}),
        isLoaded: false,
        amount: 0,
        sourceAccount: "",
        destinationAccount: "",
        accountType: "selected",
        description: "",
        destinationClient: "",
        error: false,
        errorBoolean: false,
        transfers: [],
        modal1: false,
        succes: false,
    }
  },
  created(){

    this.getClient()
  },
  mounted(){
    document.onreadystatechange = () => {
      if (document.readyState = "complete"){
        this.isLoaded = true
      }
    }
  },
  methods: {
    getClient(){
      axios.get('/api/clients/current')
      .then(response => {
        // handle success
        this.client = response.data
        this.accounts = this.client.accounts
        this.client.accounts.forEach(account => {          
          account.transactions.forEach(transaction => this.transactions.push(transaction))
        })
        this.transfers = this.transactions.filter(transaction => transaction.description.includes("Transferencia"))
      })
      .catch(function (error) {
        console.log(error);
        if (error.response.status == 401){
          window.location.href = "/public/index.html"
        }
      })
    },
    closeModal(){
      this.error = false
    },
    makeTransfer(){
      axios.post("/api/transactions", "amount="+ this.amount + "&description=" + this.description + "&sourceNumber=" + this.sourceAccount + "&destinationNumber=" + "VIN-"+this.destinationAccount,{headers:{'content-type':'application/x-www-form-urlencoded'}})
      .then(response => {
        console.log(response)
        this.succes = true
        setTimeout(()=> window.location.href="/web/accounts.html", 1500)

      })
      .catch(error => {
        let message = error.response.data
        if (message == "Not enough amount") {
          this.error = "Saldo insuficiente"
          this.errorBoolean = true
      } else if (message == "Missing data") {
          this.error = "Faltan rellenar campos"
          this.errorBoolean = true
      } else if (message == "Missing destination") {
          this.error = "Destinatario inválido"
          this.errorBoolean = true
      } else if (message == "This account do not belong to this client") {
          this.error = "Esta cuenta no pertenece al cliente seleccionado"
          this.errorBoolean = true
      } else if (message == "Missing client") {
          this.error = "Falta cliente"
          this.errorBoolean = true
      } else if (message == "Missing source account") {
          this.error = "Falta cuenta de origen"
          this.errorBoolean = true
      } else if (message == "Same Account") {
          this.error = "Selecciona otra cuenta"
          this.errorBoolean = true
      } else if (message == "Min amount"){
        this.errorBoolean = true
          this.error = "El monto no puede ser menor a 10"
      }
        console.log(error)
      })
    },
    logout(){
      axios.post('/api/logout').then(response => window.location.href="/public/index.html")
    },
    destination(){
      axios.get("/api/transactions/destination/" + "VIN-"+this.destinationAccount)
      .then(response => {
        this.destinationClient = response.data 
      })
      .catch(error => console.log(error))
    },
    clearDestination(){
      this.destinationClient = ""
    },
    formatDate(date){
      transactionDate = new Date(date)
      let options = { year: 'numeric', month: 'numeric', day: 'numeric' };
      let transactionDateFormatted = transactionDate.toLocaleDateString('en-US', options)
      return transactionDateFormatted
    },
  },
}).mount("#app")