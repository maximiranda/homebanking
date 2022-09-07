const {createApp} = Vue
const { jsPDF } = window.jspdf
var doc = new jsPDF()

createApp({
  data(){
    return {
        client : {},
        account : [],
        transactions : [],
        moneyFormat : new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }),
        isLoaded: false,
        totalLoan: 0,
        copyNumber: false,
        loans: [],
        accountNumber: "",
        startDate: null,
        endDate: null,
    }
  },
  created(){
    const params = new URLSearchParams(window.location.search)
    const id = params.get("id")
    this.loadData(id)
  },
  mounted(){
    document.onreadystatechange = () => {
      if (document.readyState = "complete"){
        this.isLoaded = true
      }
    }
  },
  methods: {
    loadData(id){
      axios.get('/api/clients/current')
      .then(response => {
        // handle success
        this.client = response.data
        this.account = this.client.accounts.find(account => account.id == id)
        this.accountNumber = this.account.number
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
        this.loans = this.client.loans
        .sort((a, b) => {
          if (a.id < b.id){
            return 1;
          };
          if (a.id > b.id){
            return -1;
          };
        })
        this.loans.forEach(loan => {
          this.totalLoan = this.totalLoan + loan.amount
        })

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
      let options = { year: 'numeric', month: 'numeric', day: 'numeric' };
      let transactionDateFormatted = transactionDate.toLocaleDateString('en-US', options)
      return transactionDateFormatted
    },
    logout(){
      axios.post('/api/logout').then(() => window.location.href="/public/index.html")
    },
    copy(text){
      this.copyNumber = true
      navigator.clipboard.writeText(text)
    },
    test(text){
      navigator.clipboard.writeText(text)
      .then(() => {
      })
      .catch(error => alert(error))
    },
    downloadMoves(){
      axios.get("/api/transactions/current",{ 
        params: {
            accountNumber: this.accountNumber,
            start: this.startDate,
            end: this.endDate

      }})
      .then(response => {
        window.html2canvas = html2canvas
        let title = document.createElement("h1")
        title.innerHTML = "Maxbank"
        title.classList.add("d-none")
        this.transactions = response.data
        let html = document.querySelector("#app")
        html.appendChild(title)
        var doc = new jsPDF("p", "pt", "a4")
        doc.fromHTML(html)
        doc.save()
        // let margin = 10
        // let scale = (doc.internal.pageSize.width - margin * 2)/ document.body.scrollWidth
        // doc.html(html, {
        //   callback: doc => {
        //     doc.save()
        //   }})
        //   doc.output("dataurlnewwindow",{filename:'hola.pdf'})

        // doc.save("hola.pdf")
      })
      .catch(error => console.log(error))

    },
  }
}).mount("#app")