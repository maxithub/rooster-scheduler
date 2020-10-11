import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavService } from '../nav.service';
import { Router } from '@angular/router';

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
export class NavComponent implements OnInit, OnDestroy {
  menus: Menu[];
  url = '';

  constructor(public nav: NavService, private route: Router) {
    this.menus = [
      new Menu('Create Job', '/job', 'create'),
      new Menu('Search Job', '/jobs', 'search'),
    ];
  }

  ngOnInit(): void {
    // this.route.url.subscribe(segments => {
    //   console.log(segments);
    // });
  }

  isActive(url: string): boolean {
    return this.route.url.startsWith(url);
  }

  ngOnDestroy(): void {
  }

}
