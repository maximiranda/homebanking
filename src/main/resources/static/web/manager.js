const {createApp} = Vue

createApp({
  data(){
    return {
      data: {},
      clients: [],
      selectedClient: {},
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
    axios.default.baseUrl = "http://localhost:8080/api/"
    this.loadData()
  },
  methods: {
    loadData(){
      axios.get('/api/clients')
      .then(response => {
        // handle success
        this.clients = response.data
        if (document.title == "Account"){

                const params = new URLSearchParams(location.search)
                const id = params.get("id")

                this.selectedClient = this.clients.find(client => client.id == id)
                console.log(this.selectedClient.accounts.length)
                this.totalBalance = this.selectedClient.accounts.reduce((a,b) =>  a = a.balance + b.balance)

            }
      })
      .catch(function (error) {
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
      axios.post('/api/clients', {
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

