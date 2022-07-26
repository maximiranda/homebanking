const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        accounts : [],
        totalBalance : 0,
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        transactions : [],
        isLoaded : false,
    }
  },
  created(){
    const params = new URLSearchParams(location.search)
    const id = params.get("id")
    this.getClient(id)
  },
  mounthed(){
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
        this.accounts = this.client.accounts
        this.accounts
        .sort((a, b) => {
          if (a.id < b.id){
            return -1;
          };
          if (a.id > b.id){
            return 1;
          };
        })
        this.client.accounts.forEach(account => {
          this.totalBalance = this.totalBalance + account.balance
          
          account.transactions.forEach(transaction => this.transactions.push(transaction))
        })
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
      let options = { year: 'numeric', month: 'numeric', day: 'numeric' };
      let transactionDateFormatted = transactionDate.toLocaleDateString('en-US', options)
      return transactionDateFormatted

    },
  }
}).mount("#app")

