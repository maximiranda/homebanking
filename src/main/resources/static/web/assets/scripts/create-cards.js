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
        cardColor: "",
        cardType: ""
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
    createCard(){
      console.log(this.cardColor,this.cardType)
      axios.post("/api/clients/current/cards", "cardType=" + this.cardType + "&cardColor=" + this.cardColor, {headers:{'content-type':'application/x-www-form-urlencoded'}})
      .then(response => {
        window.location.href="/web/cards.html"
      })
      .catch(error => console.log(error))
    },
    capitalize(str){
      const lower = str.toLowerCase()
      return str.charAt(0).toUpperCase() + lower.slice(1)
  },
    formatDate(date){
      transactionDate = new Date(date)
      let options = { year: '2-digit', month: 'numeric'};
      let transactionDateFormatted = transactionDate.toLocaleDateString('en-US', options)
      return transactionDateFormatted
    },
    logout(){
      axios.post('/api/logout').then(response => window.location.href="/public/index.html")
    },
  }
}).mount("#app")