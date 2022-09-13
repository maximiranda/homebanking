const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        isLoaded : false,
        creditCards: [],
        debitCards: [],
        hide: true,
        transactions: [],
        cardNumber : "",
        success: false,
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
        this.client.accounts.forEach(account => {          
          account.transactions.forEach(transaction => this.transactions.push(transaction))
        })
        this.debitCards = this.client.cards.filter(card => card.type == "DEBIT")
        this.creditCards = this.client.cards.filter(card => card.type == "CREDIT")
      })
      .catch(function (error) {
        console.log(error);
        if (error.response.status == 401){
          window.location.href = "/public/index.html"
        }
      })
    },
    hideBalance(){
      let eye = document.getElementById("eye")
      let slashEye = document.getElementById("slash-eye")
      let numberArray = document.querySelectorAll(".number")
      
      eye.classList.toggle("d-none")
      slashEye.classList.toggle("d-none")
      numberArray.forEach(number => {
        number.parentNode.classList.toggle("hideBalance")
        number.classList.toggle("d-none")
      })
    },
    formatDate(date){
      transactionDate = new Date(date)
      let options = { year: '2-digit', month: 'numeric'};
      let transactionDateFormatted = transactionDate.toLocaleDateString('en-US', options)
      return transactionDateFormatted
    },
    capitalize(str){
        const lower = str.toLowerCase()
        return str.charAt(0).toUpperCase() + lower.slice(1)
    },
    hideInfo(){
      if (this.hide){
        this.hide = false
      }else {
        this.hide = true
      }
    },
    rotate(){
      let cards = document.querySelectorAll(".card")
      cards.forEach(card => {
        card.classList.toggle("rotate-card")
      })
    },
    setCardNumber(cardNumber){
      this.cardNumber = cardNumber
    },
    disableCard(){
      axios.patch("/api/cards/delete", "cardNumber=" + this.cardNumber,{headers:{'content-type':'application/x-www-form-urlencoded'}})
      .then(response=>{
          this.success = "Tarjeta eliminada con exito"
          setTimeout(()=> window.location.href="/web/accounts.html",2000)
      }).catch(error => console.log(error))
    },
    logout(){
      axios.post('/api/logout').then(response => window.location.href="/public/index.html")
    },
  }
}).mount("#app")