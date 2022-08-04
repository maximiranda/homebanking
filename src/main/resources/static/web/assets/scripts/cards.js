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
    }
  },
  created(){

    const params = new URLSearchParams(location.search)
    const id = params.get("id")
    if (id == null){
        this.getClient(1)
    } else {
        this.getClient(id)
    }
  },
  mounted(){
    document.onreadystatechange = () => {
      if (document.readyState = "complete"){
        this.isLoaded = true
      }
    }
  },
  methods: {
    getClient(id){
      axios.get('/api/clients/' + id)
      .then(response => {
        // handle success
        
        this.client = response.data
        this.debitCards = this.client.cards.filter(card => card.type == "DEBIT")
        this.creditCards = this.client.cards.filter(card => card.type == "CREDIT")

      })
      .catch(function (error) {
        // handle error
        console.log(error);
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
    }
  }
}).mount("#app")