import { Component, OnInit } from '@angular/core';

export class Menu {
  constructor(
    public text: string, public url: string, public icon: string
  ) { }
}

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit {
  menus: Menu[];

  constructor() {
    this.menus = [
      new Menu('Create Job', '/job', 'create'),
      new Menu('Search Job', '/jobs', 'search'),
    ];
  }

  ngOnInit(): void {
  }

}
