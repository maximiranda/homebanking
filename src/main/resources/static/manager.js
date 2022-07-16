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
    }
  },
  created(){
    this.loadData()

  },
  methods: {
    showAndHide(event){
        event.target.parentNode.parentNode.classList.toggle("d-none")
      
    },
    loadData(){

      axios.get('/clients')
      .then(response => {
        // handle success
        this.data = response.data
        this.clients = this.data._embedded.clients
        
      })
      .catch(function (error) {
        // handle error
        console.log(error);
      })
    },
    addClient(){
      if(this.lastName != "" && this.firstName != "" && this.email.lenght != ""){
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
      let name = this.editName
      let last = this.editLast
      let email = this.editEmail

      if (this.editName == "" || this.editLast == "" || this.editEmail == ""){
        if (this.editName == ""){
            name = client.firstName
        }
        if (this.editLast == ""){
            last = client.lastName
        }
        if(this.editEmail == ""){
            email = client.email
        }
      }else{
        name = this.editName
        last = this.editLast
        email = this.editEmail
      }
      axios.patch(client._links.self.href,{
        firstName : name,
        lastName : last,
        email : email
      }).then(response => {
        this.clients.push(response.data)
        tr = document.getElementById(client._links.self.href)
        tr.classList.toggle("d-none")
        this.clients = this.clients.filter(cl => cl != client)

      })
    },
    activateForm(client){
      tr = document.getElementById(client._links.self.href)
      tr.classList.toggle("d-none")
    },
  }
}).mount("#app")

