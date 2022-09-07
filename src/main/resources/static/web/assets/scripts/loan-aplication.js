
const {createApp} = Vue

createApp({
  data(){
    return {
        client : {},
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        percentFormat : new Intl.NumberFormat('en-US', {style: 'percent', maximumFractionDigits:2}),
        isLoaded : false,
        hide: true,
        accounts: [],
        transactions: [],
        type: "",
        loans : [],
        payments: [],
        payment: 0,
        loan : [],
        amount: 0,
        interest: 0,
        ctx: null,
        data : [],
        maxAmount: 0,
        destinationAccount: "",
        error: false,
        succes: false,
    }
  },
  created(){
    this.getClient()
    this.getLoans()
  
},
mounted(){
  document.onreadystatechange = () => {
    if (document.readyState = "complete"){

        this.isLoaded = true
      }
    }
    setTimeout(this.loadChart,500)
  },
  methods: {
    loadChart(){
      this.data = [this.amount, this.amount*this.interest]
      const data = {
        labels: [
          'Monto',
          'Intereses'
        ],
        datasets: [{
          label: 'Mi prestamo',
          data: this.data,
          backgroundColor: [
            '#4367E5',
            '#00D474'
      
          ],
          hoverOffset: 2
        }]
      };
      const config = {
        type: 'doughnut',
        data: data,
      };
      this.ctx = document.getElementById('myChart').getContext('2d')
      myChart = new Chart(this.ctx, config)

    },
    getClient(){
      axios.get('/api/clients/current')
      .then(response => {
        // handle success
        
        this.client = response.data
        this.accounts = this.client.accounts
        this.client.accounts.forEach(account => {          
          account.transactions.forEach(transaction => this.transactions.push(transaction))
        })
      })
      .catch(function (error) {
        console.log(error);
        if (error.response.status == 401){
          window.location.href = "/public/index.html"
        }
      })
    },
    getLoans(){
      axios.get("/api/loans")
      .then(response => 
        {
          this.loans = response.data
        })
        .catch(error => console.log(error))
    },
    updateChart(){
      myChart.data = ({
        labels: [
          'Monto',
          'Intereses'
        ],
        datasets: [{
          label: 'Mi prestamo',
          data: [this.amount, this.amount * this.interest],
          backgroundColor: [
            '#4367E5',
            '#00D474'
      
          ],
          hoverOffset: 2
        }]
      })
      myChart.update("none")
    },
    selectType(loan){
      this.type = loan.name
      this.maxAmount = loan.maxAmount
      this.payments = loan.payments
      this.interest = loan.interest
      this.loanId = loan.id
    },
    requestLoan(){
      axios.post("/api/loans", {id:this.loanId,amount:this.amount, payments: this.payment, destinationNumberAccount: this.destinationAccount})
      .then(response => {
        console.log(response)
        this.succes = true
        setTimeout(()=>window.location.href="/web/accounts.html", 1500)

      })
      .catch(error => {
        console.log(error.response.data)
        this.error = error.response.data
        
      })
    },
    closeModal(){
      this.error = false
      window.location.reload()
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
  },
}).mount("#app")
