const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        account : [],
        transactions : [],
    }
  },
  created(){
    const params = new URLSearchParams(location.search)
    const id = params.get("id")
    this.getAccount(id)
  },
  methods: {
    getAccount(id){
      axios.get('/api/accounts/' + id)
      .then(response => {
        // handle success
        this.account = response.data
        this.transactions = this.account.transactions
        this.transactions
        .sort((a, b) => {
          if (a.id < b.id){
            return -1;
          };
          if (a.id > b.id){
            return 1;
          };
        })
      })
      .catch(function (error) {
        // handle error
        console.log(error);
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

