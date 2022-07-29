const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        account : [],
        transactions : [],
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        isLoaded: false,
    }
  },
  created(){
    const params = new URLSearchParams(location.search)
    const idClient = params.get("idClient")
    const idAccount = params.get("idAccount")
    this.loadData(idClient, idAccount)
  },
  mounted(){
    document.onreadystatechange = () => {
      if (document.readyState = "complete"){
        this.isLoaded = true
      }
    }
  },
  methods: {
    loadData(idClient, idAccount){
      axios.get('/api/clients/' + idClient)
      .then(response => {
        // handle success
        this.client = response.data
        this.account = this.client.accounts.find(account => account.id == idAccount)
        this.transactions = this.account.transactions
        this.transactions
        .sort((a, b) => {
          if (a.id < b.id){
            return 1;
          };
          if (a.id > b.id){
            return -1;
          };
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