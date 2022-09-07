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
        firstName: this.capitalize(this.firstName),
        lastName: this.capitalize(this.lastName),
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
    activateForm(client){
      let tr = document.getElementById("form" + client.id)
      tr.classList.toggle("d-none")
    },
    updateClient(client){
      let name = client.firstName
      let last = client.lastName
      let email = client.email

      if (this.editEmail == "" || this.editName == "" || this.editLast == ""){
        if (this.editEmail != ""){
          email = this.editEmail
        }
        if (this.editName != ""){
          name = this.editName
        }
        if (this.editLast != ""){
          last = this.editLast
        }
      }
      else {
        name = this.editName
        last = this.editLast
        email = this.editEmail
      }
      axios.patch("/api/clients/" + client.id,{
        id: client.id,
        firstName : name,
        lastName : last,
        email : email
      }).then(response => {
        this.clients.push(response.data)
        tr = document.getElementById("form" + client.id)
        tr.classList.toggle("d-none")
        this.clients = this.clients.filter(cl => cl != client)
      })
    },
    capitalize(str){
      const lower = str.toLowerCase()
      return str.charAt(0).toUpperCase() + lower.slice(1)
  },
  }
}).mount("#app")

