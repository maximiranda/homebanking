const {createApp} = Vue

createApp({
  data(){
    return {
      data: {},
      clients: [],
      selectedClient: {},
      selectedAccounts: {},
      lastName: "",
      firstName: "",
      email: "",
      editName: "",
      editLast: "",
      editEmail: "",
      totalBalance : 0,
    }
  },
  created(){
    this.loadData()
  },
  methods: {
    loadData(){
      axios.get('/clients')
      .then(response => {
        // handle success
        this.data = response.data
        this.clients = this.data._embedded.clients
        if (document.title == "Account"){

                const params = new URLSearchParams(location.search)
                const id = params.get("id")

                this.selectedClient = this.clients.find(client => client._links.self.href == id)
                axios.get(this.selectedClient._links.accounts.href)
                .then(response => {
                    this.selectedAccounts = response.data._embedded.accounts
                    this.totalBalance = this.selectedAccounts.reduce((a, b) => a.balance + b.balance)
                })
                .catch(error => {
                    console.log(error)
                })
            }
      })
      .catch(function (error) {
        // handle error
        console.log(error);
      })
      axios.get('/accounts')
      .then(response => {
        this.accounts = response.data._embedded.accounts
      })
      .catch(error =>{
         // handle error
         console.log(error);
      })
    },
    addClient(){
      if(this.lastName != "" && this.firstName != "" && this.email != ""){
        this.postClient()
      }else {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: 'Necesitas llenar todos los campos para poder continuar!',
        })
      }

    },
    postClient(){
      axios.post('/clients', {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        }).then((response) => {
              this.clients.push(response.data)
              this.firstName = ""
              this.lastName= ""
              this.email = ""
          }).catch(function (error) {
              console.log(error);
          });
    },
    deleteClient(event, client){
      event.target.parentNode.parentNode.remove()
      axios.delete(client._links.self.href)
    },
    activateForm(client){
      tr = document.getElementById(client._links.self.href)
      tr.classList.toggle("d-none")
    },
    updateClient(client){
      axios.patch(client._links.self.href,{
        firstName : this.editName,
        lastName : this.editLast,
        email : this.editEmail
      }).then(response => {
        this.clients.push(response.data)
        tr = document.getElementById(client._links.self.href)
        tr.classList.toggle("d-none")
        this.clients = this.clients.filter(cl => cl != client)

      })
    },

  }
}).mount("#app")

