const {createApp} = Vue

createApp({
  data(){
    return {
        client: {},
        accounts: [],
        transactions: [],
        loans: [],
        totalBalance: 0,
        moneyFormat: new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        isLoaded: false,
        totalLoan: 0,
        email: "",
        accountsLength : 0,
        success: false,
        accountType:"",
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
        console.log(response)
        this.client = response.data
        this.accounts = this.client.accounts
        this.loans = this.client.loans
        this.accountsLength = this.accounts.length
        this.accounts
        .sort((a, b) => {
          if (a.id < b.id){
            return -1;
          };
          if (a.id > b.id){
            return 1;
          };
        })
        this.client.loans.forEach(loan => {
          this.totalLoan = this.totalLoan + loan.amount
        })

        this.client.accounts.forEach(account => {
          this.totalBalance = this.totalBalance + account.balance
          
          account.transactions.forEach(transaction => this.transactions.push(transaction))
        })
        this.transactions.sort((a,b) => {
           if (a.id < b.id){
               return 1;
              };
          if (a.id > b.id){
               return -1;
          };

    })
      })
      .catch(function (error) {
        console.log(error);
        if (error.response.status == 401){
          window.location.href = "/public/index.html"
        }
      })
    },
    addAccount(){
        axios.post('/api/clients/current/accounts', "accountType=" + this.accountType, {headers:{'Content-Type':'application/x-www-form-urlencoded'}} )
        .then(response => {
          console.log(response)
          this.success = true
          setTimeout(()=> window.location.reload(), 2000)
          
        })
        .catch(error => console.log(error))
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
    logout(){
      axios.post('/api/logout').then(response => window.location.href="/public/index.html")
    },
    
  }
}).mount("#app")