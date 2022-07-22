const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        totalBalance : 0,
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
    }
  },
  created(){
    const params = new URLSearchParams(location.search)
    const id = params.get("id")
    this.getClient(id)
  },
  methods: {
    getClient(id){
      axios.get('/api/clients/' + id)
      .then(response => {
        // handle success
        this.client = response.data

        this.client.accounts.forEach(account => {
          this.totalBalance = this.totalBalance + account.balance
        })
      })
      .catch(function (error) {
        // handle error
        console.log(error);
      })
    },
  }
}).mount("#app")

